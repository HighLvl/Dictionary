package ru.cherepanov.apps.dictionary.domain.repository

import io.reactivex.Completable
import io.reactivex.Observable
import ru.cherepanov.apps.dictionary.domain.model.Filter

interface PreferencesRepository {
    val filter: Observable<Filter>
    fun saveFilter(filter: Filter): Completable
}