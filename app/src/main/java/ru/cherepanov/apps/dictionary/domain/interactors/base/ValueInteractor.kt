package ru.cherepanov.apps.dictionary.domain.interactors.base

import io.reactivex.Observable

class ValueInteractor<T : Any> : SimpleSubjectInteractor<T, T>() {
    override fun createObservable(args: T): Observable<T> {
        return Observable.just(args)
    }
}