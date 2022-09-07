package ru.cherepanov.apps.dictionary.domain.interactors

import io.reactivex.Single

abstract class UpdateDataInteractor<T> {
    abstract operator fun invoke(args: T): Single<Unit>
}