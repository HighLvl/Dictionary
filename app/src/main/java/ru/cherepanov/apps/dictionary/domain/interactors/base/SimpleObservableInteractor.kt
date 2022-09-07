package ru.cherepanov.apps.dictionary.domain.interactors.base

import io.reactivex.Observable

abstract class SimpleObservableInteractor<R : Any> : ObservableInteractor<R, R>() {
    override fun Observable<R>.transformObservable(): Observable<R> {
        return this
    }
}