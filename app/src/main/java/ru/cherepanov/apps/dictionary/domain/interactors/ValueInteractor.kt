package ru.cherepanov.apps.dictionary.domain.interactors

import io.reactivex.Observable

class ValueInteractor<T : Any> : SubjectInteractor<T, T, T>() {
    override fun Observable<T>.transformObservable(): Observable<T> {
        return this
    }

    override fun createObservable(args: T): Observable<T> {
        return Observable.just(args)
    }
}