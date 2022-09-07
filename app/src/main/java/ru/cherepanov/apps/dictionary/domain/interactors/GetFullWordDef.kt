package ru.cherepanov.apps.dictionary.domain.interactors

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.repository.DictRepository
import ru.cherepanov.apps.dictionary.ui.FormattedWordDef
import ru.cherepanov.apps.dictionary.ui.toFormatted
import javax.inject.Inject

class GetFullWordDef @Inject constructor(private val repository: DictRepository) :
    ResourceSubjectInteractor<GetFullWordDef.Args, FormattedWordDef>() {

    override fun createObservable(args: Args): Observable<FormattedWordDef> {
        return repository.getFullDefById(args.defId)
            .map {
                it.toFormatted(isDetails = true)
            }.subscribeOn(Schedulers.io())
            .toObservable()
    }

    data class Args(val defId: DefId)
}
