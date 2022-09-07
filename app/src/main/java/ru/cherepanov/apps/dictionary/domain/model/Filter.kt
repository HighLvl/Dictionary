package ru.cherepanov.apps.dictionary.domain.model

data class Filter(val searchMode: SearchMode) {
    enum class SearchMode {
        PREFIX, FUZZY
    }
}
