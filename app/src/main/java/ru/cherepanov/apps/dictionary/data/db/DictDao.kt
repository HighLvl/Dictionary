package ru.cherepanov.apps.dictionary.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

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

    @Query(
        "SELECT * FROM definitions WHERE id_title = :title AND " +
                "id_langNum = :langNum AND " +
                "id_senseNum = :senseNum AND " +
                "id_glossNum = :glossNum AND " +
                "isFull = 1 LIMIT 1"
    )
    fun getFullDefById(
        title: String,
        langNum: Int,
        senseNum: Int,
        glossNum: Int
    ): Maybe<WordDefEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wordDef: WordDefEntity)

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
    fun insert(entities: List<WordDefEntity>)

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
    ): Single<Unit>

    @Query("SELECT * FROM definitions WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavorites(): Flowable<List<WordDefEntity>>
}