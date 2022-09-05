package ru.cherepanov.apps.dictionary.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.cherepanov.apps.dictionary.data.LocalSourceImpl
import ru.cherepanov.apps.dictionary.data.db.DictDao
import ru.cherepanov.apps.dictionary.data.db.DictDb
import ru.cherepanov.apps.dictionary.domain.repository.LocalSource
import javax.inject.Singleton

@Module(includes = [DbModule.BindsModule::class])
@InstallIn(SingletonComponent::class)
class DbModule {
    @Singleton
    @Provides
    fun provideDictDao(db: DictDb): DictDao {
        return db.dao
    }

    @Singleton
    @Provides
    fun provideDictDb(@ApplicationContext context: Context): DictDb {
        return Room.databaseBuilder(context, DictDb::class.java, "dictionary.db").build()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    interface BindsModule {
        @Singleton
        @Binds
        fun bindLocalSource(localSource: LocalSourceImpl): LocalSource
    }
}