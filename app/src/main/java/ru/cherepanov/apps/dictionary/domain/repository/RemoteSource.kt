package ru.cherepanov.apps.dictionary.domain.repository

import io.reactivex.Single
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import ru.cherepanov.apps.dictionary.domain.model.WordDefRemoteData

interface RemoteSource {
    fun findTitlesHavingPrefix(prefix: String): Single<List<String>>
    fun findSimilarWordTitles(searchTerm: String): Single<List<String>>
    fun getShortDefsByTitle(title: String): Single<List<WordDefRemoteData>>
    fun getFullDefById(id: DefId): Single<WordDefRemoteData>
    fun getRandomWordShortDefs(): Single<List<WordDefRemoteData>>
}