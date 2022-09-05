package ru.cherepanov.apps.dictionary.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.schedulers.Schedulers
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import ru.cherepanov.apps.dictionary.domain.repository.DictRepository
import ru.cherepanov.apps.dictionary.ui.FormattedWordDef
import ru.cherepanov.apps.dictionary.ui.base.viewModel.BaseViewModel
import ru.cherepanov.apps.dictionary.ui.base.viewModel.Resource
import ru.cherepanov.apps.dictionary.ui.toFormatted
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val repository: DictRepository) :
    BaseViewModel() {
    private val _uiState = MutableLiveData(Resource.loading(listOf<FormattedWordDef>()))
    val uiState: LiveData<Resource<List<FormattedWordDef>>> = _uiState

    init {
        repository.getFavorites()
            .subscribeOn(Schedulers.io())
            .subscribeAndObserveOnMainThread(disposables) {
                _uiState.value = Resource.success(it.map(WordDef::toFormatted))
            }
    }

    fun onRemoveFromFavorites(id: DefId) {
        repository.removeFromFavorites(id).subscribeOnIO(disposables)
    }
}