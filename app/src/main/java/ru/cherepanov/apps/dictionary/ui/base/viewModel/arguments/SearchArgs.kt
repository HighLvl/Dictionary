package ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class SearchArgs(val searchTerm: String? = null): Arguments() {
    override fun encodeToString(): String {
        return Json.encodeToString(this)
    }
}
