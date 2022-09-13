package ru.cherepanov.apps.dictionary.data

import androidx.paging.PagingSource
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import ru.cherepanov.apps.dictionary.data.db.*
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import ru.cherepanov.apps.dictionary.domain.model.WordDefRemoteData
import ru.cherepanov.apps.dictionary.domain.model.mapToWordDef
import ru.cherepanov.apps.dictionary.domain.repository.LocalSource
import javax.inject.Inject

class LocalSourceImpl @Inject constructor(private val dao: DictDao) : LocalSource {
    override fun cache(wordDefs: List<WordDefRemoteData>): Completable {
        val updatedAt = System.currentTimeMillis()
        val wordDefEntities = wordDefs
            .map(WordDefRemoteData::mapToWordDef)
            .map { it.mapToEntity(updatedAt) }
        return dao.insert(wordDefEntities)
    }

    override fun cache(wordDef: WordDefRemoteData): Completable {
        val updatedAt = System.currentTimeMillis()
        val wordDefEntity = wordDef.mapToWordDef(isFull = true)
            .mapToEntity(updatedAt = updatedAt)
        return Completable.fromCallable { dao.cache(wordDefEntity) }
    }

    override fun getByIdObservable(id: DefId): Observable<WordDef> {
        return with(id.mapToEntityId()) {
            dao.getByIdObservable(
                title,
                langNum,
                senseNum,
                glossNum
            )
        }.map(WordDefEntity::mapToWordDef)
    }

    override fun setFavorite(id: DefId, value: Boolean): Completable {
        return with(id.mapToEntityId()) {
            dao.setFavorite(
                title,
                langNum,
                senseNum,
                glossNum,
                value
            )
        }
    }

    override fun getAllByTitle(title: String): Observable<List<WordDef>> {
        return dao.getAllByTitleObservable(title).map { it.map(WordDefEntity::mapToWordDef) }
    }

    override fun isShortDefsCached(title: String): Maybe<Boolean> {
        return dao.isShortDefsInDb(title)
    }

    override fun getFullDefById(id: DefId): Maybe<WordDef> {
        return with(id.mapToEntityId()) {
            dao.getWordDefByIdMaybe(
                title,
                langNum,
                senseNum,
                glossNum
            )
        }.map { it.mapToWordDef() }
    }

    override fun getFavorites(): PagingSource<Int, WordDefEntity> {
        return dao.getFavorites()
    }

    override fun isFullDefCached(id: DefId): Maybe<Boolean> {
        return with(id.mapToEntityId()) { dao.isFullDefInDb(title, langNum, senseNum, glossNum) }
    }
}