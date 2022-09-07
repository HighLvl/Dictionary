package ru.cherepanov.apps.dictionary.domain.interactors

import io.reactivex.Completable
import ru.cherepanov.apps.dictionary.domain.interactors.base.UpdateDataInteractor
import ru.cherepanov.apps.dictionary.domain.model.Filter
import ru.cherepanov.apps.dictionary.domain.repository.PreferencesRepository
import javax.inject.Inject

class UpdateFilter @Inject constructor(private val preferencesRepository: PreferencesRepository) :
    UpdateDataInteractor<Filter>() {
    override fun invoke(args: Filter): Completable {
        return preferencesRepository.saveFilter(args)
    }
}