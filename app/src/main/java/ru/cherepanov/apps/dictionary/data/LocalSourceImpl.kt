package ru.cherepanov.apps.dictionary.data

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.cherepanov.apps.dictionary.data.db.*
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import ru.cherepanov.apps.dictionary.domain.repository.LocalSource
import javax.inject.Inject

class LocalSourceImpl @Inject constructor(private val dao: DictDao) : LocalSource {
    override fun cache(wordDefs: List<WordDef>) {
        val updatedAt = System.currentTimeMillis()
        dao.insert(wordDefs.map { it.mapToEntity(updatedAt) })
    }

    override fun cache(wordDef: WordDef) {
        val updatedAt = System.currentTimeMillis()
        dao.insert(wordDef.mapToEntity(updatedAt))
    }

    override fun getByIdFlowable(id: DefId): Flowable<WordDef> {
        return with(id.mapToEntityId()) {
            dao.getByIdFlowable(
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

    override fun getAllByTitle(title: String): Flowable<List<WordDef>> {
        return dao.getAllByTitleFlowable(title).map { it.map(WordDefEntity::mapToWordDef) }
    }

    override fun isShortDefsCached(title: String): Maybe<Boolean> {
        return dao.isShortDefsInDb(title)
    }

    override fun getFullDefById(id: DefId): Maybe<WordDef> {
        return with(id.mapToEntityId()) {
            dao.getFullDefById(
                title,
                langNum,
                senseNum,
                glossNum
            )
        }.map { it.mapToWordDef() }
    }

    override fun getFavorites(): Flowable<List<WordDef>> {
        return dao.getFavorites().map { it.map(WordDefEntity::mapToWordDef) }
    }

    override fun isFullDefCached(id: DefId): Maybe<Boolean> {
        return with(id.mapToEntityId()) { dao.isFullDefInDb(title, langNum, senseNum, glossNum) }
    }
}