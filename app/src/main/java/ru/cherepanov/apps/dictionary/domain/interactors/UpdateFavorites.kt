package ru.cherepanov.apps.dictionary.domain.interactors

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.repository.DictRepository
import javax.inject.Inject

class UpdateFavorites @Inject constructor(private val repository: DictRepository) : UpdateDataInteractor<UpdateFavorites.Args>() {
    override fun invoke(args: Args): Single<Unit> {
        return if (args.isFavorite) {
            repository.addToFavorites(args.defId)
        } else {
            repository.removeFromFavorites(args.defId)
        }.subscribeOn(Schedulers.io())
    }

    data class Args(val defId: DefId, val isFavorite: Boolean)
}