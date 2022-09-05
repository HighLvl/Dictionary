package ru.cherepanov.apps.dictionary.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.schedulers.Schedulers
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import ru.cherepanov.apps.dictionary.domain.repository.DictRepository
import ru.cherepanov.apps.dictionary.ui.FormattedWordDef
import ru.cherepanov.apps.dictionary.ui.base.viewModel.BaseViewModel
import ru.cherepanov.apps.dictionary.ui.base.viewModel.Resource
import ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments.DetailsArgs
import ru.cherepanov.apps.dictionary.ui.toFormatted
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DictRepository
) : BaseViewModel(savedStateHandle) {
    private val _uiState = MutableLiveData(Resource.loading(WordDef().toFormattedFullDef()))
    val uiState: LiveData<Resource<FormattedWordDef>> = _uiState

    private val defId = getArgs<DetailsArgs>().defId

    init {
        onLoadDetails(defId)
    }

    private fun onLoadDetails(defId: DefId) = runRepeatable {
        repository.getFullDefById(defId)
            .map {
                Resource.success(it.toFormattedFullDef())
            }
            .startWith(Resource.loading(WordDef(defId).toFormattedFullDef()))
            .onErrorReturn {
                Resource.error(WordDef(defId).toFormattedFullDef(), error = it)
            }
            .subscribeOn(Schedulers.io())
            .subscribeAndObserveOnMainThread(disposables) {
                _uiState.value = it
            }
    }

    private fun WordDef.toFormattedFullDef(): FormattedWordDef {
        return toFormatted(details = true)
    }

    fun onAddToFavorites() {
        repository.addToFavorites(defId).subscribeOnIO(disposables)
    }

    fun onRemoveFromFavorites() {
        _uiState.value = _uiState.value!!.copy(_uiState.value!!.data.copy(isFavorite = false))
        repository.removeFromFavorites(defId).subscribeOnIO(disposables)
    }
}