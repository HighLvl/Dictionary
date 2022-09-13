package ru.cherepanov.apps.dictionary.ui.favorites

import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.cherepanov.apps.dictionary.domain.interactors.GetFavorites
import ru.cherepanov.apps.dictionary.domain.interactors.UpdateFavorites
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.ui.FormattedWordDef
import ru.cherepanov.apps.dictionary.ui.base.viewModel.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavorites: GetFavorites,
    private val updateFavorites: UpdateFavorites
) : BaseViewModel<PagingData<FormattedWordDef>>(initialState = PagingData.from(emptyList())) {

    val pagingData = uiState.asFlow().cachedIn(viewModelScope)

    init {
        subscribeUiState()
    }

    private fun subscribeUiState() {
        getFavorites.observable()
            .subscribeAndObserveOnMainThread(disposables) { favoritesPagingData ->
                state = favoritesPagingData
            }
    }

    fun onRemoveFromFavorites(id: DefId) {
        updateFavorites(UpdateFavorites.Args(id, false))
            .subscribe(disposables)
    }
}