package ru.cherepanov.apps.dictionary.di.modules

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.cherepanov.apps.dictionary.data.PreferencesRepositoryImpl
import ru.cherepanov.apps.dictionary.domain.repository.DictRepository
import ru.cherepanov.apps.dictionary.domain.repository.PreferencesRepository
import ru.cherepanov.apps.dictionary.domain.repository.RepositoryImpl
import javax.inject.Singleton

@Module(includes = [NetworkModule::class, DbModule::class])
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindDictRepository(repository: RepositoryImpl): DictRepository

    @Binds
    @Singleton
    fun bindPreferencesRepository(repository: PreferencesRepositoryImpl): PreferencesRepository
}