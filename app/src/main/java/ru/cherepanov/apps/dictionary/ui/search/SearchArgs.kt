package ru.cherepanov.apps.dictionary.ui.search

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments.Arguments

@Serializable
data class SearchArgs(val searchTerm: String? = null): Arguments() {
    override fun encodeToString(): String {
        return Json.encodeToString(this)
    }
}
