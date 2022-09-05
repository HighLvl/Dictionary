package ru.cherepanov.apps.dictionary.ui.searchList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import ru.cherepanov.apps.dictionary.domain.repository.DictRepository
import ru.cherepanov.apps.dictionary.ui.base.viewModel.BaseViewModel
import ru.cherepanov.apps.dictionary.ui.base.viewModel.Resource
import ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments.SearchListArgs
import ru.cherepanov.apps.dictionary.ui.base.viewModel.updateData
import ru.cherepanov.apps.dictionary.ui.toFormatted
import javax.inject.Inject

@HiltViewModel
class SearchListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DictRepository,
) : BaseViewModel(savedStateHandle) {
    private val uiEvent = BehaviorSubject.create<UIStateEvent>()

    private val shortDefsSourceEvent = PublishSubject.create<ShortDefsSourceEvent>()

    private val _uiState = MutableLiveData(Resource.loading(SearchListUiState()))
    val uiState: LiveData<Resource<SearchListUiState>> = _uiState

    init {
        subscribeUiStateFlowable()

        getArgs<SearchListArgs>().let { (defId, title) ->
            if (defId != null) {
                onBringIntoView(defId)
            } else {
                onLoadRandomWord()
            }
            if (title != null) {
                onLoadWordByTitle(title)
            }
        }
    }

    private fun subscribeUiStateFlowable() {
        Flowable.merge(uiEvent.toFlowable(BackpressureStrategy.BUFFER), getShortDefsFlowable())
            .subscribeAndObserveOnMainThread(disposables) {
                _uiState.value = mapEventToState(_uiState.value!!, it)
            }
    }

    private fun getShortDefsFlowable() =
        shortDefsSourceEvent.toFlowable(BackpressureStrategy.LATEST)
            .observeOn(Schedulers.io())
            .switchMap { event ->
                when (event) {
                    is ShortDefsSourceEvent.WordByTitle -> repository.getShortDefsByTitle(event.title)
                        .map { UIStateEvent.NewShortDefs(it) }
                    is ShortDefsSourceEvent.RandomWord -> repository.getRandomWordShortDefs()
                        .map { UIStateEvent.NewShortDefs(it) }
                    is ShortDefsSourceEvent.BringIntoView -> {
                        var bringIntoView: DefId? = event.id
                        repository.getShortDefsByTitle(event.id.title)
                            .map {
                                UIStateEvent.NewShortDefs(it, bringIntoView = bringIntoView).also {
                                    bringIntoView = null
                                }
                            }
                    }
                }.map { it as UIStateEvent }
                    .onErrorReturn { UIStateEvent.Error(it) }
                    .subscribeOn(Schedulers.io())
            }


    private fun mapEventToState(
        oldState: Resource<SearchListUiState>,
        event: UIStateEvent
    ): Resource<SearchListUiState> = when (event) {
        is UIStateEvent.NewShortDefs ->
            Resource.success(
                oldState.data.copy(
                    wordTitle = event.shortDefs.first().id.title,
                    shortDefs = event.shortDefs.map(WordDef::toFormatted),
                    defId = event.bringIntoView
                )
            )
        is UIStateEvent.IntoViewBrought -> oldState.updateData {
            it?.copy(defId = null)
        }
        is UIStateEvent.Loading -> Resource.loading(data = oldState.data.copy(wordTitle = event.title))
        is UIStateEvent.Error -> Resource.error(
            error = event.throwable,
            data = oldState.data
        )
    }

    private fun onBringIntoView(id: DefId) {
        runRepeatable {
            shortDefsSourceEvent.onNext(ShortDefsSourceEvent.BringIntoView(id))
            uiEvent.onNext(UIStateEvent.Loading(id.title))
        }
    }

    private fun onLoadWordByTitle(title: String) {
        runRepeatable {
            shortDefsSourceEvent.onNext(ShortDefsSourceEvent.WordByTitle(title))
            uiEvent.onNext(UIStateEvent.Loading(title))
        }
    }

    fun onLoadRandomWord() {
        runRepeatable {
            shortDefsSourceEvent.onNext(ShortDefsSourceEvent.RandomWord)
            uiEvent.onNext(UIStateEvent.Loading())
        }
    }

    fun onAddToFavorites(id: DefId) {
        repository.addToFavorites(id).subscribeOnIO(disposables)
    }

    fun onRemoveFromFavorites(id: DefId) {
        repository.removeFromFavorites(id).subscribeOnIO(disposables)
    }

    fun onIntoViewBrought() {
        uiEvent.onNext(UIStateEvent.IntoViewBrought)
    }

    private sealed interface ShortDefsSourceEvent {
        data class WordByTitle(val title: String) : ShortDefsSourceEvent
        object RandomWord : ShortDefsSourceEvent
        data class BringIntoView(val id: DefId) : ShortDefsSourceEvent
    }

    private sealed interface UIStateEvent {
        data class Loading(val title: String = "") : UIStateEvent
        data class Error(val throwable: Throwable) : UIStateEvent
        object IntoViewBrought : UIStateEvent
        data class NewShortDefs(val shortDefs: List<WordDef>, val bringIntoView: DefId? = null) :
            UIStateEvent
    }
}