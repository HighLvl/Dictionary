package ru.cherepanov.apps.dictionary.domain.interactors.base

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

abstract class SubjectInteractor<T : Any, R : Any, O : Any> {
    private val eventSubject = PublishSubject.create<T>()
    private val resultSubject = BehaviorSubject.create<O>()

    private var disposable: Disposable? = null

    fun observable(initialValue: O? = null): Observable<O> {
        if (disposable == null) {
            disposable = eventSubject.switchMap {
                createObservable(it).transformObservable()
            }.let {
                if (initialValue != null) it.startWith(initialValue)
                else it
            }.subscribe(resultSubject::onNext)
        }
        return resultSubject.doOnDispose {
            disposable?.dispose()
        }
    }

    protected abstract fun Observable<R>.transformObservable(): Observable<O>

    protected abstract fun createObservable(args: T): Observable<R>

    open operator fun invoke(args: T) {
        eventSubject.onNext(args)
    }
}