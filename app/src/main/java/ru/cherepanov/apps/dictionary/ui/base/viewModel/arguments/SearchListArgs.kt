package ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.cherepanov.apps.dictionary.domain.model.DefId

@Serializable
data class SearchListArgs(val defId: DefId? = null, val title: String? = null) : Arguments() {
    override fun encodeToString(): String {
        return Json.encodeToString(this)
    }
}