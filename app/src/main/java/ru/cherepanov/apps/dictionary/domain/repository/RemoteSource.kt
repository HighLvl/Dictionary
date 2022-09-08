package ru.cherepanov.apps.dictionary.domain.repository

import io.reactivex.Single
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDef

interface RemoteSource {
    fun findTitlesHavingPrefix(prefix: String): Single<List<String>>
    fun findSimilarWordTitles(searchTerm: String): Single<List<String>>
    fun getShortDefsByTitle(title: String): Single<List<WordDef>>
    fun getFullDefById(id: DefId): Single<WordDef>
    fun getRandomWordShortDefs(): Single<List<WordDef>>
}