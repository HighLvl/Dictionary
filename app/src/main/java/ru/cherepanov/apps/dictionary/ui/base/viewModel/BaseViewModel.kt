package ru.cherepanov.apps.dictionary.ui.base.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments.Arguments

abstract class BaseViewModel(protected val savedStateHandle: SavedStateHandle? = null) : ViewModel() {
    private var lastActionCallback: () -> Unit = {}
    protected val disposables = CompositeDisposable()

    fun runRepeatable(callback: () -> Unit) {
        lastActionCallback = callback.also { it.invoke() }
    }

    fun retry() {
        lastActionCallback()
    }

    protected inline fun <reified T : Arguments> getArgs(): T {
        return Arguments.decodeFromString(savedStateHandle!!.get<String>(Arguments.ARG_KEY)!!)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    protected fun <T> Flowable<T>.subscribeAndObserveOnMainThread(compositeDisposable: CompositeDisposable, consumer: (T) -> Unit) {
        observeOn(AndroidSchedulers.mainThread()).subscribe(consumer).also {
            compositeDisposable.add(it)
        }
    }

    fun <T> Observable<T>.subscribeAndObserveOnMainThread(
        compositeDisposable: CompositeDisposable,
        consumer: (T) -> Unit
    ) {
        observeOn(AndroidSchedulers.mainThread()).subscribe(consumer).also {
            compositeDisposable.add(it)
        }
    }

    fun <T> Single<T>.subscribeOnIO(compositeDisposable: CompositeDisposable) {
        subscribeOn(Schedulers.io()).subscribe().also {
            compositeDisposable.add(it)
        }
    }
}