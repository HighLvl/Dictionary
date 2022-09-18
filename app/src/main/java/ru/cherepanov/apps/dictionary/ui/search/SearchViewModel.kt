package ru.cherepanov.apps.dictionary.ui.search

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import ru.cherepanov.apps.dictionary.domain.interactors.GetFilter
import ru.cherepanov.apps.dictionary.domain.interactors.GetSuggestions
import ru.cherepanov.apps.dictionary.domain.interactors.UpdateFilter
import ru.cherepanov.apps.dictionary.domain.interactors.base.ValueInteractor
import ru.cherepanov.apps.dictionary.domain.model.Filter
import ru.cherepanov.apps.dictionary.domain.model.Resource
import ru.cherepanov.apps.dictionary.ui.base.viewModel.BaseViewModel
import ru.cherepanov.apps.dictionary.ui.base.viewModel.Status
import ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments.Arguments
import ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments.SearchArgs
import javax.inject.Inject

data class SearchState(
    val searchTerm: String = "",
    val suggestions: List<String> = emptyList(),
    val filter: Filter = Filter(Filter.SearchMode.PREFIX),
    val status: Status = Status.LOADING
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSuggestions: GetSuggestions,
    private val getFilter: GetFilter,
    private val updateFilter: UpdateFilter
) : BaseViewModel<SearchState>(savedStateHandle, SearchState()) {
    private val changeSearchTerm = ValueInteractor<String>()
    private val changeArgs = ValueInteractor<Bundle>()

    init {
        subscribeUiState()
        getSuggestionsOnFilterChange()
        getSuggestionsOnSearchTermChange()
        changeSearchTermOnNewArgs()
    }

    private fun subscribeUiState(
        initialSearchTerm: String = "",
        initialSuggestionsResource: Resource<List<String>> = Resource.success(emptyList()),
        initialFilter: Filter = Filter(searchMode = Filter.SearchMode.PREFIX)
    ) {
        Observable.combineLatest(
            changeSearchTerm.observable(initialSearchTerm),
            getSuggestions.observable(initialSuggestionsResource),
            getFilter.observable(initialFilter)
        ) { searchTerm, suggestionsResource, filter ->
            SearchState(
                searchTerm = searchTerm,
                suggestions = when (suggestionsResource) {
                    is Resource.Success -> suggestionsResource.data
                    else -> emptyList()
                },
                filter = filter,
                status = suggestionsResource.mapToStatus()
            )
        }.subscribeAndObserveOnMainThread(disposables) {
            state = it
        }
    }

    private fun getSuggestionsOnSearchTermChange() {
        changeSearchTerm.observable()
            .subscribeAndObserveOnMainThread(disposables) { searchTerm ->
                runRetriable {
                    getSuggestions(GetSuggestions.Args(searchTerm, state.filter))
                }
            }
    }

    private fun getSuggestionsOnFilterChange() {
        getFilter.observable()
            .subscribeAndObserveOnMainThread(disposables) { filter ->
                runRetriable {
                    getSuggestions(GetSuggestions.Args(state.searchTerm, filter))
                }
            }
    }

    private fun changeSearchTermOnNewArgs() {
        changeArgs.observable()
            .subscribeAndObserveOnMainThread(disposables) { arguments ->
                val stringArgs = arguments.getString(Arguments.ARG_KEY)!!
                val searchArgs = Arguments.decodeFromString<SearchArgs>(stringArgs)
                if (searchArgs.searchTerm != null) {
                    changeSearchTerm(searchArgs.searchTerm)
                }
            }
    }

    fun onChangeFilter(filter: Filter) {
        updateFilter(filter)
    }

    fun onChangeSearchTerm(searchTerm: String) {
        changeSearchTerm(searchTerm)
    }

    fun onSetArgs(arguments: Bundle) {
        changeArgs(arguments)
    }
}