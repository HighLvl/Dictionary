package ru.cherepanov.apps.dictionary.domain.interactors

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import ru.cherepanov.apps.dictionary.domain.repository.DictRepository
import ru.cherepanov.apps.dictionary.ui.FormattedWordDef
import ru.cherepanov.apps.dictionary.ui.toFormatted
import javax.inject.Inject

class GetShortDefs @Inject constructor(private val repository: DictRepository) :
    ResourceSubjectInteractor<GetShortDefs.Args, GetShortDefs.Result>() {
    override fun createObservable(args: Args): Observable<Result> {
        return when (args.mode) {
            Mode.Random -> repository.getRandomWordShortDefs().map {
                Result(shortDefs = it.map(WordDef::toFormatted))
            }
            is Mode.ByTitle -> repository.getShortDefsByTitle(args.mode.title).map {
                Result(shortDefs = it.map(WordDef::toFormatted))
            }
            is Mode.BringIntoView -> {
                var bringIntoView: DefId? = args.mode.defId
                repository.getShortDefsByTitle(args.mode.defId.title)
                    .map {
                        Result(shortDefs = it.map(WordDef::toFormatted), bringIntoView).also {
                            bringIntoView = null
                        }
                    }
            }
        }
            .subscribeOn(Schedulers.io())
            .toObservable()
    }

    data class Result(val shortDefs: List<FormattedWordDef>, val bringIntoView: DefId? = null)

    data class Args(val mode: Mode)

    sealed interface Mode {
        object Random : Mode
        data class ByTitle(val title: String) : Mode
        data class BringIntoView(val defId: DefId) : Mode
    }
}