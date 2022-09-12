package ru.cherepanov.apps.dictionary.data.db

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable

@Dao
interface DictDao {
    @Query(
        "SELECT * FROM definitions WHERE id_title = :title AND " +
                "id_langNum = :langNum AND " +
                "id_senseNum = :senseNum AND " +
                "id_glossNum = :glossNum LIMIT 1"
    )
    fun getByIdObservable(
        title: String,
        langNum: Int,
        senseNum: Int,
        glossNum: Int
    ): Observable<WordDefEntity>

    @Query(
        "SELECT * FROM definitions WHERE id_title = :title AND " +
                "id_langNum = :langNum AND " +
                "id_senseNum = :senseNum AND " +
                "id_glossNum = :glossNum AND " +
                "isFull = 1 LIMIT 1"
    )
    fun getWordDefByIdMaybe(
        title: String,
        langNum: Int,
        senseNum: Int,
        glossNum: Int
    ): Maybe<WordDefEntity>

    @Query(
        "SELECT * FROM definitions WHERE id_title = :title AND " +
                "id_langNum = :langNum AND " +
                "id_senseNum = :senseNum AND " +
                "id_glossNum = :glossNum"
    )
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
    fun getAllByTitleObservable(title: String): Observable<List<WordDefEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entities: List<WordDefEntity>): Completable

    @Query("SELECT 1 FROM definitions WHERE id_title = :title LIMIT 1")
    fun isShortDefsInDb(title: String): Maybe<Boolean>

    @Query(
        "UPDATE definitions SET isFavorite = :value, updatedAt = :updatedAt WHERE id_title = :title AND " +
                "id_langNum = :langNum AND " +
                "id_senseNum = :senseNum AND " +
                "id_glossNum = :glossNum"
    )
    fun setFavorite(
        title: String,
        langNum: Int,
        senseNum: Int,
        glossNum: Int,
        value: Boolean,
        updatedAt: Long
    ): Completable

    fun setFavorite(
        title: String,
        langNum: Int,
        senseNum: Int,
        glossNum: Int,
        value: Boolean
    ): Completable {
        val updatedAt = System.currentTimeMillis()
        return setFavorite(title, langNum, senseNum, glossNum, value, updatedAt)
    }

    @Query("SELECT * FROM definitions WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavorites(): Observable<List<WordDefEntity>>
}