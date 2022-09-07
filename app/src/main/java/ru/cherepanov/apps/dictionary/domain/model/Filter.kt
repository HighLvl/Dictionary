package ru.cherepanov.apps.dictionary.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Filter(val searchMode: SearchMode) {
    enum class SearchMode {
        PREFIX, FUZZY
    }
}
