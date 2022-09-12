package ru.cherepanov.apps.dictionary.data

import io.reactivex.Single
import ru.cherepanov.apps.dictionary.data.network.DictionaryWebService
import ru.cherepanov.apps.dictionary.data.network.WordDefDto
import ru.cherepanov.apps.dictionary.data.network.mapToWordDefRemoteData
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDefRemoteData
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
    ): Single<List<WordDefRemoteData>> {
        return webService.getShortDefsByTitle(title)
            .map { it.map(WordDefDto::mapToWordDefRemoteData) }
    }

    override fun getFullDefById(id: DefId): Single<WordDefRemoteData> {
        return with(id) {
            webService.getFullDefById(title, langNum ?: 0, senseNum ?: 0, glossNum ?: 0)
                .map { it.mapToWordDefRemoteData() }
        }
    }

    override fun getRandomWordShortDefs(): Single<List<WordDefRemoteData>> {
        return webService.getRandonWordShortDefs()
            .map { it.map(WordDefDto::mapToWordDefRemoteData) }
    }
}