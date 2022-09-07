package ru.cherepanov.apps.dictionary.domain.repository

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDef

interface LocalSource {
    fun cache(wordDefs: List<WordDef>)
    fun cache(wordDef: WordDef)
    fun getByIdFlowable(id: DefId): Flowable<WordDef>
    fun setFavorite(id: DefId, value: Boolean): Completable
    fun getAllByTitle(title: String): Flowable<List<WordDef>>
    fun isShortDefsCached(title: String): Maybe<Boolean>
    fun getFullDefById(id: DefId): Maybe<WordDef>
    fun getFavorites(): Flowable<List<WordDef>>
    fun isFullDefCached(id: DefId): Maybe<Boolean>
}