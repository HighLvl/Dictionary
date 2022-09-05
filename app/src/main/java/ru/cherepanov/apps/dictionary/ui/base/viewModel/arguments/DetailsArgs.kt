package ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.cherepanov.apps.dictionary.domain.model.DefId

@Serializable
data class DetailsArgs(val defId: DefId) : Arguments() {
    override fun encodeToString(): String {
        return Json.encodeToString(this)
    }
}
