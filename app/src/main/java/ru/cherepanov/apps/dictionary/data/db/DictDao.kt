package ru.cherepanov.apps.dictionary.data.db

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable

@Dao
interface DictDao {
    @Query(GET_BY_ID_QUERY)
    fun getByIdObservable(
        title: String,
        langNum: Int,
        senseNum: Int,
        glossNum: Int
    ): Observable<WordDefEntity>

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

    @Query(IS_FULL_DEF_IN_DB_QUERY)
    fun isFullDefInDb(
        title: String,
        langNum: Int,
        senseNum: Int,
        glossNum: Int
    ): Maybe<Boolean>

    @Query(GET_ALL_BY_TITLE_QUERY)
    fun getAllByTitleObservable(title: String): Observable<List<WordDefEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entities: List<WordDefEntity>): Completable

    @Query(IS_SHORT_DEFS_IN_DB_QUERY)
    fun isShortDefsInDb(title: String): Maybe<Boolean>

    @Query(SET_FAVORITE_QUERY)
    fun setFavorite(
        title: String,
        langNum: Int,
        senseNum: Int,
        glossNum: Int,
        value: Boolean
    ): Completable

    @Query(GET_FAVORITES_QUERY)
    fun getFavorites(): Observable<List<WordDefEntity>>

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

        const val SET_FAVORITE_QUERY =
            "UPDATE definitions SET isFavorite = :value WHERE id_title = :title AND " +
                    "id_langNum = :langNum AND " +
                    "id_senseNum = :senseNum AND " +
                    "id_glossNum = :glossNum"

        const val GET_ALL_BY_TITLE_QUERY = "SELECT * FROM definitions WHERE id_title = :title"

        const val IS_SHORT_DEFS_IN_DB_QUERY =
            "SELECT 1 FROM definitions WHERE id_title = :title LIMIT 1"

        const val GET_FAVORITES_QUERY =
            "SELECT * FROM definitions WHERE isFavorite = 1 ORDER BY updatedAt DESC"

        const val IS_FULL_DEF_IN_DB_QUERY =
            "SELECT 1 FROM definitions WHERE id_title = :title AND " +
                    "id_langNum = :langNum AND " +
                    "id_senseNum = :senseNum AND " +
                    "id_glossNum = :glossNum AND " +
                    "isFull = 1 LIMIT 1"

        const val GET_BY_ID_QUERY = "SELECT * FROM definitions WHERE id_title = :title AND " +
                "id_langNum = :langNum AND " +
                "id_senseNum = :senseNum AND " +
                "id_glossNum = :glossNum LIMIT 1"
    }
}