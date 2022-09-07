package ru.cherepanov.apps.dictionary.domain.interactors

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

abstract class SubjectInteractor<T : Any, R : Any, O : Any> {
    private val eventSubject = PublishSubject.create<T>()
    private val resultSubject = BehaviorSubject.create<O>()

    private val disposable = eventSubject.switchMap {
        createObservable(it).transformObservable()
    }.subscribe(resultSubject::onNext)

    fun observable(initialValue: O? = null): Observable<O> = resultSubject.apply {
        if (initialValue != null) onNext(initialValue)
    }.doOnDispose {
        disposable.dispose()
    }

    protected abstract fun Observable<R>.transformObservable(): Observable<O>

    protected abstract fun createObservable(args: T): Observable<R>

    open operator fun invoke(args: T) {
        eventSubject.onNext(args)
    }
}