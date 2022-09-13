package ru.cherepanov.apps.dictionary.domain.repository

import androidx.paging.PagingSource
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import ru.cherepanov.apps.dictionary.data.db.WordDefEntity
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import ru.cherepanov.apps.dictionary.domain.model.WordDefRemoteData

interface LocalSource {
    fun cache(wordDefs: List<WordDefRemoteData>): Completable
    fun cache(wordDef: WordDefRemoteData): Completable
    fun getByIdObservable(id: DefId): Observable<WordDef>
    fun setFavorite(id: DefId, value: Boolean): Completable
    fun getAllByTitle(title: String): Observable<List<WordDef>>
    fun isShortDefsCached(title: String): Maybe<Boolean>
    fun getFullDefById(id: DefId): Maybe<WordDef>
    fun getFavorites(): PagingSource<Int, WordDefEntity>
    fun isFullDefCached(id: DefId): Maybe<Boolean>
}