package ru.cherepanov.apps.dictionary.domain.repository

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.Filter
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import javax.inject.Inject

class RepositoryImpl @Inject constructor(localSource: LocalSource, remoteSource: RemoteSource) :
    DictRepository(localSource, remoteSource) {
    override fun getRandomWordShortDefs(): Flowable<List<WordDef>> =
        remoteSource.getRandomWordShortDefs()
            .flatMap { shortDefs ->
                val title = shortDefs.first().title
                localSource.isShortDefsCached(title)
                    .switchIfEmpty(
                        localSource.cache(shortDefs).toSingleDefault(true)
                    ).map { title }
            }
            .toFlowable()
            .switchMap {
                localSource.getAllByTitle(it)
            }.doOnError {
                println(it)
            }

    override fun getShortDefsByTitle(title: String): Flowable<List<WordDef>> =
        localSource.isShortDefsCached(title)
            .switchIfEmpty(
                remoteSource.getShortDefsByTitle(title)
                    .flatMap { shortDefs ->
                        localSource.cache(shortDefs).toSingleDefault(true)
                    }
            )
            .toFlowable()
            .switchMap {
                localSource.getAllByTitle(title)
            }

    override fun addToFavorites(id: DefId): Completable =
        localSource.setFavorite(id, true).andThen(
            localSource.isFullDefCached(id)
                .switchIfEmpty(
                    remoteSource.getFullDefById(id).flatMap { fullDef ->
                        localSource.cache(fullDef).toSingleDefault(true)
                    }.onErrorReturnItem(false)
                ).ignoreElement()
        )

    override fun removeFromFavorites(id: DefId) =
        localSource.setFavorite(id, false)

    override fun findWordTitles(searchTerm: String, filter: Filter): Single<List<String>> {
        return when (filter.searchMode) {
            Filter.SearchMode.FUZZY -> remoteSource.findSimilarWordTitles(searchTerm)
            Filter.SearchMode.PREFIX -> remoteSource.findTitlesHavingPrefix(searchTerm)
        }
    }

    override fun getFullDefById(id: DefId): Flowable<WordDef> =
        localSource.isFullDefCached(id)
            .switchIfEmpty(
                remoteSource.getFullDefById(id)
                    .flatMap { fullDef ->
                        localSource.cache(fullDef).toSingleDefault(true)
                    }
                    .onErrorReturnItem(false)
            )
            .toFlowable()
            .switchMap {
                localSource.getByIdFlowable(id)
            }

    override fun getFavorites(): Flowable<List<WordDef>> {
        return localSource.getFavorites()
    }
}