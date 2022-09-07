package ru.cherepanov.apps.dictionary.domain.repository

import io.reactivex.Flowable
import io.reactivex.Single
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.Filter
import ru.cherepanov.apps.dictionary.domain.model.WordDef

abstract class DictRepository(
    protected val localSource: LocalSource,
    protected val remoteSource: RemoteSource
) {
    abstract fun getRandomWordShortDefs(): Flowable<List<WordDef>>
    abstract fun getShortDefsByTitle(title: String): Flowable<List<WordDef>>
    abstract fun addToFavorites(id: DefId): Single<Unit>
    abstract fun removeFromFavorites(id: DefId): Single<Unit>
    abstract fun findWordTitles(searchTerm: String, filter: Filter): Single<List<String>>
    abstract fun getFullDefById(id: DefId): Flowable<WordDef>
    abstract fun getFavorites(): Flowable<List<WordDef>>
}