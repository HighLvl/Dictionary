package ru.cherepanov.apps.dictionary.domain.interactors

import io.reactivex.Observable
import ru.cherepanov.apps.dictionary.domain.interactors.base.SimpleObservableInteractor
import ru.cherepanov.apps.dictionary.domain.model.Filter
import ru.cherepanov.apps.dictionary.domain.repository.PreferencesRepository
import javax.inject.Inject

class GetFilter @Inject constructor(private val preferencesRepository: PreferencesRepository) :
    SimpleObservableInteractor<Filter>() {
    override fun createObservable(): Observable<Filter> {
        return preferencesRepository.filter
    }
}

