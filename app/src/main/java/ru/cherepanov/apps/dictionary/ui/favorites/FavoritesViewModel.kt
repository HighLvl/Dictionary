package ru.cherepanov.apps.dictionary.ui.favorites

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.cherepanov.apps.dictionary.domain.interactors.GetFavorites
import ru.cherepanov.apps.dictionary.domain.interactors.UpdateFavorites
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.Resource
import ru.cherepanov.apps.dictionary.ui.FormattedWordDef
import ru.cherepanov.apps.dictionary.ui.base.viewModel.BaseViewModel
import ru.cherepanov.apps.dictionary.ui.base.viewModel.Status
import javax.inject.Inject

data class FavoritesState(
    val favorites: List<FormattedWordDef> = emptyList(),
    val status: Status = Status.LOADING
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavorites: GetFavorites,
    private val updateFavorites: UpdateFavorites
) :
    BaseViewModel<FavoritesState>(initialState = FavoritesState()) {

    init {
        subscribeUiState()
        onGetFavorites()
    }

    private fun subscribeUiState(
        initialFavoritesResource: Resource<List<FormattedWordDef>> = Resource.loading()
    ) {
        getFavorites.observable(initialFavoritesResource)
            .subscribeAndObserveOnMainThread(disposables) { favoritesResource ->
                state = FavoritesState(
                    when (favoritesResource) {
                        is Resource.Success -> favoritesResource.data
                        else -> emptyList()
                    },
                    status = favoritesResource.mapToStatus()
                )
            }
    }

    private fun onGetFavorites() {
        getFavorites(Unit)
    }

    fun onRemoveFromFavorites(id: DefId) {
        updateFavorites(UpdateFavorites.Args(id, false))
            .subscribe(disposables)
    }
}