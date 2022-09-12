package ru.cherepanov.apps.dictionary.domain.interactors

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ru.cherepanov.apps.dictionary.domain.interactors.base.ResourceObservableInteractor
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import ru.cherepanov.apps.dictionary.domain.repository.DictRepository
import ru.cherepanov.apps.dictionary.ui.FormattedWordDef
import ru.cherepanov.apps.dictionary.ui.toFormatted
import javax.inject.Inject

class GetFavorites @Inject constructor(private val repository: DictRepository) :
    ResourceObservableInteractor<List<FormattedWordDef>>() {
    override fun createObservable(): Observable<List<FormattedWordDef>> {
        return repository.getFavorites()
            .map {
                it.map(WordDef::toFormatted)
            }.subscribeOn(Schedulers.io())
    }
}