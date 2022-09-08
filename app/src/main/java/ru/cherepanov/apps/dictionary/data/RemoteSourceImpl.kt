package ru.cherepanov.apps.dictionary.data

import io.reactivex.Single
import ru.cherepanov.apps.dictionary.data.network.DictionaryWebService
import ru.cherepanov.apps.dictionary.data.network.WordDefDto
import ru.cherepanov.apps.dictionary.data.network.mapToFullDef
import ru.cherepanov.apps.dictionary.data.network.mapToShortDef
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import ru.cherepanov.apps.dictionary.domain.repository.RemoteSource
import javax.inject.Inject

class RemoteSourceImpl @Inject constructor(private val webService: DictionaryWebService) :
    RemoteSource {
    override fun findTitlesHavingPrefix(prefix: String): Single<List<String>> {
        return webService.findTitlesHavingPrefix(prefix)
    }

    override fun findSimilarWordTitles(searchTerm: String): Single<List<String>> {
        return webService.findSimilarWordTitles(searchTerm)
    }

    override fun getShortDefsByTitle(
        title: String
    ): Single<List<WordDef>> {
        return webService.getShortDefsByTitle(title).map { it.map(WordDefDto::mapToShortDef) }
    }

    override fun getFullDefById(id: DefId): Single<WordDef> {
        return with(id) {
            webService.getFullDefById(title, langNum ?: 0, senseNum ?: 0, glossNum ?: 0)
                .map { it.mapToFullDef() }
        }
    }

    override fun getRandomWordShortDefs(): Single<List<WordDef>> {
        return webService.getRandonWordShortDefs().map { it.map(WordDefDto::mapToShortDef) }
    }
}