package ru.cherepanov.apps.dictionary.domain.interactors

import androidx.paging.PagingData
import io.reactivex.Observable
import ru.cherepanov.apps.dictionary.domain.interactors.base.SimpleObservableInteractor
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import ru.cherepanov.apps.dictionary.domain.repository.DictRepository
import javax.inject.Inject

class GetFavorites @Inject constructor(private val repository: DictRepository) :
    SimpleObservableInteractor<PagingData<WordDef>>() {
    override fun createObservable(): Observable<PagingData<WordDef>> {
        return repository.getFavorites()
    }
}