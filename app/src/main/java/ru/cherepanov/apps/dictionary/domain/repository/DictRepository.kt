package ru.cherepanov.apps.dictionary.domain.repository

import androidx.paging.PagingData
import androidx.paging.PagingSource
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import ru.cherepanov.apps.dictionary.data.db.WordDefEntity
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.Filter
import ru.cherepanov.apps.dictionary.domain.model.WordDef

abstract class DictRepository(
    protected val localSource: LocalSource,
    protected val remoteSource: RemoteSource
) {
    abstract fun getRandomWordShortDefs(): Observable<List<WordDef>>
    abstract fun getShortDefsByTitle(title: String): Observable<List<WordDef>>
    abstract fun addToFavorites(id: DefId): Completable
    abstract fun removeFromFavorites(id: DefId): Completable
    abstract fun findWordTitles(searchTerm: String, filter: Filter): Single<List<String>>
    abstract fun getFullDefById(id: DefId): Observable<WordDef>
    abstract fun getFavorites(): Observable<PagingData<WordDef>>
}