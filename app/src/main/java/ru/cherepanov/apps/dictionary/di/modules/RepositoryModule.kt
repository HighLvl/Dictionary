package ru.cherepanov.apps.dictionary.di.modules

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.cherepanov.apps.dictionary.domain.repository.RepositoryImpl
import ru.cherepanov.apps.dictionary.domain.repository.DictRepository
import javax.inject.Singleton

@Module(includes = [NetworkModule::class, DbModule::class])
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindDictRepository(repository: RepositoryImpl): DictRepository
}