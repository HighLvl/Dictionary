package ru.cherepanov.apps.dictionary.ui.base.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.cherepanov.apps.dictionary.domain.model.Resource
import ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments.Arguments

abstract class BaseViewModel<T : Any>(
    protected val savedStateHandle: SavedStateHandle? = null,
    initialState: T
) :
    ViewModel() {
    private var lastActionCallback: () -> Unit = {}
    protected val disposables = CompositeDisposable()

    private val _uiState = MutableLiveData(initialState)
    val uiState: LiveData<T> = _uiState

    protected var state: T
        get() = _uiState.value!!
        set(value) {
            _uiState.value = value
        }

    fun runRetriable(callback: () -> Unit) {
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

    protected fun Resource<*>.mapToStatus() =
        when (this) {
            is Resource.Loading -> Status.LOADING
            is Resource.Success -> Status.SUCCESS
            is Resource.Error -> Status.ERROR
        }

    fun <T> Observable<T>.subscribeAndObserveOnMainThread(
        compositeDisposable: CompositeDisposable,
        consumer: (T) -> Unit
    ) {
        observeOn(AndroidSchedulers.mainThread()).subscribe(consumer).also {
            compositeDisposable.add(it)
        }
    }


    fun Completable.subscribe(compositeDisposable: CompositeDisposable) {
        subscribe().also { compositeDisposable.add(it) }
    }
}