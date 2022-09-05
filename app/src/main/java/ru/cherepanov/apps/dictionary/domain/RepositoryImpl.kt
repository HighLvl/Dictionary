package ru.cherepanov.apps.dictionary.domain

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import ru.cherepanov.apps.dictionary.domain.repository.DictRepository
import ru.cherepanov.apps.dictionary.domain.repository.LocalSource
import ru.cherepanov.apps.dictionary.domain.repository.RemoteSource
import javax.inject.Inject

class RepositoryImpl @Inject constructor(localSource: LocalSource, remoteSource: RemoteSource) :
    DictRepository(localSource, remoteSource) {
    override fun getRandomWordShortDefs(): Flowable<List<WordDef>> =
        remoteSource.getShortGlossListByRandomTitle()
            .flatMap { shortDefs ->
                val title = shortDefs.first().id.title
                localSource.isShortDefsCached(title)
                    .switchIfEmpty(
                        Maybe.fromCallable {
                            localSource.cache(shortDefs)
                            true
                        }
                    )
                    .map { title }
                    .toSingle()
            }
            .toFlowable()
            .switchMap {
                localSource.getAllByTitle(it)
            }

    override fun getShortDefsByTitle(title: String): Flowable<List<WordDef>> =
        localSource.isShortDefsCached(title)
            .switchIfEmpty(
                remoteSource.getShortGlossList(title)
                    .map { shortDefs ->
                        localSource.cache(shortDefs)
                        true
                    }.toMaybe()
            )
            .toFlowable()
            .switchMap {
                localSource.getAllByTitle(title)
            }

    override fun addToFavorites(id: DefId): Single<Unit> =
        localSource.isFullDefCached(id)
            .switchIfEmpty(
                remoteSource.getFullGloss(id).map { fullDef ->
                    localSource.cache(fullDef)
                    true
                }.toMaybe()
            ).toSingle()
            .flatMap {
                localSource.setFavorite(id, true)
            }

    override fun removeFromFavorites(id: DefId) =
        localSource.setFavorite(id, false)

    override fun getWordTitlesStartsWith(prefix: String): Single<List<String>> {
        return remoteSource.findTitlesHavingPrefix(prefix)
    }

    override fun getFullDefById(id: DefId): Flowable<WordDef> =
        localSource.getFullDefById(id)
            .switchIfEmpty(
                remoteSource.getFullGloss(id)
                    .map { fullDef ->
                        localSource.cache(fullDef)
                        fullDef
                    }.onErrorReturn { WordDef(id = id, isFull = false) }
            )
            .toFlowable()
            .switchMap {
                localSource.getByIdFlowable(id)
            }

    override fun getFavorites(): Flowable<List<WordDef>> {
        return localSource.getFavorites()
    }
}