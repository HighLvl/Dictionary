package ru.cherepanov.apps.dictionary.domain.interactors

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ru.cherepanov.apps.dictionary.domain.interactors.base.ResourceSubjectInteractor
import ru.cherepanov.apps.dictionary.domain.model.Filter
import ru.cherepanov.apps.dictionary.domain.repository.DictRepository
import javax.inject.Inject

class GetSuggestions @Inject constructor(private val repository: DictRepository) :
    ResourceSubjectInteractor<GetSuggestions.Args, List<String>>() {
    override fun createObservable(args: Args): Observable<List<String>> {
        return if (args.searchTerm.isBlank()) {
            Observable.just(emptyList())
        } else {
            repository.findWordTitles(args.searchTerm, args.filter)
                .toObservable()
                .subscribeOn(Schedulers.io())
        }
    }

    data class Args(val searchTerm: String, val filter: Filter)
}