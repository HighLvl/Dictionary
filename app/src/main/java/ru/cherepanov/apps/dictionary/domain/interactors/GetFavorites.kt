package ru.cherepanov.apps.dictionary.domain.interactors

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.map
import androidx.paging.rxjava2.observable
import io.reactivex.Observable
import kotlinx.coroutines.flow.map
import ru.cherepanov.apps.dictionary.data.db.mapToWordDef
import ru.cherepanov.apps.dictionary.domain.interactors.base.SimpleObservableInteractor
import ru.cherepanov.apps.dictionary.domain.repository.DictRepository
import ru.cherepanov.apps.dictionary.ui.FormattedWordDef
import ru.cherepanov.apps.dictionary.ui.toFormatted
import javax.inject.Inject

class GetFavorites @Inject constructor(private val repository: DictRepository) :
    SimpleObservableInteractor<PagingData<FormattedWordDef>>() {

    override fun createObservable(): Observable<PagingData<FormattedWordDef>> {
        return Pager(
            config = PagingConfig(
                pageSize = 16,
                prefetchDistance = 16,
                initialLoadSize = 16,
                maxSize = 64
            )
        ) {
            repository.getFavorites()
        }.observable.map { pagingData ->
            pagingData.map { entity ->
                entity.mapToWordDef().toFormatted()
            }
        }
    }
}