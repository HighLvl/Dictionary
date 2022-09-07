package ru.cherepanov.apps.dictionary.domain.interactors.base

import io.reactivex.Observable

abstract class SimpleSubjectInteractor<T : Any, R : Any> : SubjectInteractor<T, R, R>() {
    override fun Observable<R>.transformObservable(): Observable<R> {
        return this
    }
}