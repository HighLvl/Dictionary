package ru.cherepanov.apps.dictionary.domain.interactors.base

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

abstract class ObservableInteractor<R : Any, O : Any> {
    private var disposable: Disposable? = null
    private val resultSubject = BehaviorSubject.create<O>()

    fun observable(initialValue: O? = null): Observable<O> {
        if (disposable == null) {
            disposable = createObservable().transformObservable().let {
                if (initialValue != null)
                    it.startWith(initialValue)
                else it
            }.subscribe(resultSubject::onNext)
        }
        return resultSubject.doOnDispose {
            disposable?.dispose()
        }
    }

    protected abstract fun createObservable(): Observable<R>

    protected abstract fun Observable<R>.transformObservable(): Observable<O>
}