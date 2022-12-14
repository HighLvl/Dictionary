package ru.cherepanov.apps.dictionary.ui.searchList

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import ru.cherepanov.apps.dictionary.domain.interactors.GetShortDefs
import ru.cherepanov.apps.dictionary.domain.interactors.UpdateFavorites
import ru.cherepanov.apps.dictionary.domain.interactors.base.ValueInteractor
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.Resource
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import ru.cherepanov.apps.dictionary.ui.base.viewModel.BaseViewModel
import ru.cherepanov.apps.dictionary.ui.base.viewModel.Status
import javax.inject.Inject

data class SearchListState(
    val shortDefs: List<WordDef> = emptyList(),
    val title: String = "",
    val defId: DefId? = null,
    val status: Status = Status.LOADING
)

@HiltViewModel
class SearchListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val updateFavorites: UpdateFavorites,
    private val getShortDefs: GetShortDefs,
) : BaseViewModel<SearchListState>(savedStateHandle, SearchListState()) {
    private val setTitle = ValueInteractor<String>()

    init {
        subscribeUiState()
        setTitleOnNewShortDefs()
        getArgs<SearchListArgs>().let { (defId, title) ->
            when {
                defId != null -> onBringIntoView(defId)
                title != null -> onLoadWordByTitle(title)
                else -> onLoadRandomWord()
            }
        }
    }

    private fun subscribeUiState(
        initialShortDefsResource: Resource<GetShortDefs.Result> = Resource.loading(),
        initialTitle: String = ""
    ) {
        Observable.combineLatest(
            getShortDefs.observable(initialShortDefsResource),
            setTitle.observable(initialTitle)
        ) { shortDefsResource, title ->
            SearchListState(
                shortDefs = when (shortDefsResource) {
                    is Resource.Success -> shortDefsResource.data.shortDefs
                    else -> emptyList()
                },
                defId = when (shortDefsResource) {
                    is Resource.Success -> shortDefsResource.data.bringIntoView
                    else -> null
                },
                title = title,
                status = shortDefsResource.mapToStatus()
            )
        }.subscribeAndObserveOnMainThread(disposables) {
            state = it
        }
    }

    private fun setTitleOnNewShortDefs() {
        getShortDefs.observable()
            .subscribeAndObserveOnMainThread(disposables) { shortDefsResource ->
                if (shortDefsResource is Resource.Success) {
                    shortDefsResource.data.shortDefs.firstOrNull()?.let {
                        setTitle(it.id.title)
                    }
                }
            }
    }

    private fun onBringIntoView(id: DefId) {
        runRetriable {
            setTitle(id.title)
            getShortDefs(GetShortDefs.Args(mode = GetShortDefs.Mode.BringIntoView(id)))
        }
    }

    private fun onLoadWordByTitle(title: String) {
        runRetriable {
            setTitle(title)
            getShortDefs(GetShortDefs.Args(mode = GetShortDefs.Mode.ByTitle(title)))
        }
    }

    fun onLoadRandomWord() {
        runRetriable {
            getShortDefs(GetShortDefs.Args(mode = GetShortDefs.Mode.Random))
        }
    }

    fun onAddToFavorites(id: DefId) {
        updateFavorites(UpdateFavorites.Args(defId = id, isFavorite = true))
            .subscribe(disposables)
    }

    fun onRemoveFromFavorites(id: DefId) {
        updateFavorites(UpdateFavorites.Args(defId = id, isFavorite = false))
            .subscribe(disposables)
    }
}