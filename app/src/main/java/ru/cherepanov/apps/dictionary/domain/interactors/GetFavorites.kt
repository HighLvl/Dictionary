package ru.cherepanov.apps.dictionary.domain.interactors

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava2.observable
import io.reactivex.Observable
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
                pageSize = 32,
                enablePlaceholders = true
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