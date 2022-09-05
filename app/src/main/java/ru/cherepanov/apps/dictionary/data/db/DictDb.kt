package ru.cherepanov.apps.dictionary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WordDefEntity::class], version = 1, exportSchema = false)
abstract class DictDb : RoomDatabase() {
    abstract val dao: DictDao
}