package ru.cherepanov.apps.dictionary.data.db

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface DictDao {
    @Query(
        "SELECT * FROM definitions WHERE id_title = :title AND " +
                "id_langNum = :langNum AND " +
                "id_senseNum = :senseNum AND " +
                "id_glossNum = :glossNum LIMIT 1"
    )
    fun getByIdFlowable(
        title: String,
        langNum: Int,
        senseNum: Int,
        glossNum: Int
    ): Flowable<WordDefEntity>

    @Query(GET_FULL_DEF_BY_ID_QUERY)
    fun getWordDefByIdMaybe(
        title: String,
        langNum: Int,
        senseNum: Int,
        glossNum: Int
    ): Maybe<WordDefEntity>

    @Query(GET_WORD_DEF_BY_ID_QUERY)
    fun getWordDefById(
        title: String,
        langNum: Int,
        senseNum: Int,
        glossNum: Int
    ): WordDefEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wordDef: WordDefEntity)

    @Transaction
    fun cache(wordDefEntity: WordDefEntity) {
        val wordDef = with(wordDefEntity.id) {
            getWordDefById(title, langNum, senseNum, glossNum)
        }
        val newWordDefEntity = if (wordDef != null) {
            wordDefEntity.copy(isFavorite = wordDef.isFavorite)
        } else {
            wordDefEntity
        }
        insert(newWordDefEntity)
    }

    @Query(
        "SELECT 1 FROM definitions WHERE id_title = :title AND " +
                "id_langNum = :langNum AND " +
                "id_senseNum = :senseNum AND " +
                "id_glossNum = :glossNum AND " +
                "isFull = 1 LIMIT 1"
    )
    fun isFullDefInDb(
        title: String,
        langNum: Int,
        senseNum: Int,
        glossNum: Int
    ): Maybe<Boolean>

    @Query("SELECT * FROM definitions WHERE id_title = :title")
    fun getAllByTitleFlowable(title: String): Flowable<List<WordDefEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entities: List<WordDefEntity>): Completable

    @Query("SELECT 1 FROM definitions WHERE id_title = :title LIMIT 1")
    fun isShortDefsInDb(title: String): Maybe<Boolean>

    @Query(
        "UPDATE definitions SET isFavorite = :value WHERE id_title = :title AND " +
                "id_langNum = :langNum AND " +
                "id_senseNum = :senseNum AND " +
                "id_glossNum = :glossNum"
    )
    fun setFavorite(
        title: String,
        langNum: Int,
        senseNum: Int,
        glossNum: Int,
        value: Boolean
    ): Completable

    @Query("SELECT * FROM definitions WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavorites(): Flowable<List<WordDefEntity>>

    private companion object {
        const val GET_FULL_DEF_BY_ID_QUERY =
            "SELECT * FROM definitions WHERE id_title = :title AND " +
                    "id_langNum = :langNum AND " +
                    "id_senseNum = :senseNum AND " +
                    "id_glossNum = :glossNum AND " +
                    "isFull = 1 LIMIT 1"

        const val GET_WORD_DEF_BY_ID_QUERY =
            "SELECT * FROM definitions WHERE id_title = :title AND " +
                    "id_langNum = :langNum AND " +
                    "id_senseNum = :senseNum AND " +
                    "id_glossNum = :glossNum"
    }
}