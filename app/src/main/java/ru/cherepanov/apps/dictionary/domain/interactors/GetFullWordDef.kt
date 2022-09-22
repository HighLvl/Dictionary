package ru.cherepanov.apps.dictionary.domain.interactors

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ru.cherepanov.apps.dictionary.domain.interactors.base.ResourceSubjectInteractor
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import ru.cherepanov.apps.dictionary.domain.repository.DictRepository
import javax.inject.Inject

class GetFullWordDef @Inject constructor(private val repository: DictRepository) :
    ResourceSubjectInteractor<GetFullWordDef.Args, WordDef>() {

    override fun createObservable(args: Args): Observable<WordDef> {
        return repository.getFullDefById(args.defId)
            .subscribeOn(Schedulers.io())
    }

    data class Args(val defId: DefId)
}
