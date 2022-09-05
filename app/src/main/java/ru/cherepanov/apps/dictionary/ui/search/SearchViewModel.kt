package ru.cherepanov.apps.dictionary.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import ru.cherepanov.apps.dictionary.domain.repository.DictRepository
import ru.cherepanov.apps.dictionary.ui.base.viewModel.BaseViewModel
import ru.cherepanov.apps.dictionary.ui.base.viewModel.Resource
import ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments.SearchArgs
import javax.inject.Inject

data class SearchState(
    val searchTerm: String = "",
    val suggestions: List<String> = emptyList()
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DictRepository
) : BaseViewModel(savedStateHandle) {
    private val uiEvent = BehaviorSubject.create<UIEvent>()
    private val _uiState = MutableLiveData(Resource(SearchState("", emptyList())))
    val uiState: LiveData<Resource<SearchState>> = _uiState

    init {
        subscribeUiState()
        onFetchSuggestions(getArgs<SearchArgs>().searchTerm)
    }

    private fun subscribeUiState() {
        uiEvent.switchMap { event ->
            when (event) {
                is UIEvent.FetchSuggestions -> fetchSuggestions(event.searchTerm)
            }
        }
            .subscribeAndObserveOnMainThread(disposables) {
                _uiState.value = it
            }
    }

    private fun fetchSuggestions(searchTerm: String): Observable<Resource<SearchState>> {
        return repository.getWordTitlesStartsWith(searchTerm).toObservable().map {
            Resource.success(
                SearchState(
                    searchTerm = searchTerm,
                    suggestions = it
                )
            )
        }.startWith(
            Resource.loading(SearchState(searchTerm = searchTerm))
        ).onErrorReturn { throwable ->
            Resource.error(
                SearchState(searchTerm = searchTerm),
                error = throwable
            )
        }.subscribeOn(Schedulers.io())
    }


    fun onFetchSuggestions(searchTerm: String) {
        runRepeatable {
            if (searchTerm.isNotBlank())
                uiEvent.onNext(UIEvent.FetchSuggestions(searchTerm))
        }
    }

    private sealed class UIEvent {
        data class FetchSuggestions(val searchTerm: String) : UIEvent()
    }
}