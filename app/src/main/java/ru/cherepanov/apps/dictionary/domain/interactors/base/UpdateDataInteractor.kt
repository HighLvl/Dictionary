package ru.cherepanov.apps.dictionary.domain.interactors.base

import io.reactivex.Completable

abstract class UpdateDataInteractor<T> {
    abstract operator fun invoke(args: T): Completable
}