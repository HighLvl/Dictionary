package ru.cherepanov.apps.dictionary.ui.details

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.cherepanov.apps.dictionary.domain.interactors.GetFullWordDef
import ru.cherepanov.apps.dictionary.domain.interactors.UpdateFavorites
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.Resource
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import ru.cherepanov.apps.dictionary.ui.base.viewModel.BaseViewModel
import ru.cherepanov.apps.dictionary.ui.base.viewModel.Status
import javax.inject.Inject

data class DetailsState(
    val wordDef: WordDef = WordDef(DefId()),
    val status: Status = Status.LOADING
)

@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getFullWordDef: GetFullWordDef,
    private val updateFavorites: UpdateFavorites
) : BaseViewModel<DetailsState>(savedStateHandle, DetailsState()) {

    private val defId = getArgs<DetailsArgs>().defId

    init {
        subscribeUiState()
        onLoadDetails(defId)
    }

    private fun subscribeUiState(
        initialWordDefResource: Resource<WordDef> = Resource.loading()
    ) {
        getFullWordDef.observable(initialWordDefResource)
            .subscribeAndObserveOnMainThread(disposables) { wordDefResource ->
                state = DetailsState(
                    wordDef = when (wordDefResource) {
                        is Resource.Success -> wordDefResource.data
                        else -> WordDef(defId)
                    },
                    status = wordDefResource.mapToStatus()
                )
            }
    }

    private fun onLoadDetails(defId: DefId) = runRetriable {
        getFullWordDef.invoke(GetFullWordDef.Args(defId))
    }

    fun onAddToFavorites() {
        updateFavorites(UpdateFavorites.Args(defId, isFavorite = true))
            .subscribe(disposables)
    }

    fun onRemoveFromFavorites() {
        updateFavorites(UpdateFavorites.Args(defId, isFavorite = false))
            .subscribe(disposables)
    }
}