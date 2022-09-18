package ru.cherepanov.apps.dictionary.domain.interactors.base

import io.reactivex.Observable

class ValueInteractor<T : Any> : SimpleSubjectInteractor<T, T>() {
    private var prevArgs: T? = null
    override fun createObservable(args: T): Observable<T> {
        return Observable.just(args)
    }

    override fun invoke(args: T) {
        if (prevArgs != args) {
            super.invoke(args)
            prevArgs = args
        }
    }
}