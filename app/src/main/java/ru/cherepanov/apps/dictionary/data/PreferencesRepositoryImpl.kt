package ru.cherepanov.apps.dictionary.data

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.rxjava2.rxPreferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.cherepanov.apps.dictionary.domain.model.Filter
import ru.cherepanov.apps.dictionary.domain.repository.PreferencesRepository
import javax.inject.Inject

private val Context.dataStore by rxPreferencesDataStore("appData")

class PreferencesRepositoryImpl @Inject constructor(@ApplicationContext context: Context) :
    PreferencesRepository {
    private val dataStore = context.dataStore

    @OptIn(ExperimentalCoroutinesApi::class)
    override val filter: Observable<Filter> = dataStore.data()
        .doOnSubscribe {
            println()
        }
        .filter {
            it.contains(KEY_FILTER)
        }.map {
            it[KEY_FILTER]
        }.map {
            Json.decodeFromString<Filter>(it)
        }.toObservable()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun saveFilter(filter: Filter): Completable {
        return dataStore.updateDataAsync {
            Single.fromCallable {
                it.toMutablePreferences().apply {
                    set(KEY_FILTER, Json.encodeToString(filter))
                }
            }
        }.ignoreElement()
    }

    private companion object {
        val KEY_FILTER = stringPreferencesKey("KEY_FILTER")
    }
}