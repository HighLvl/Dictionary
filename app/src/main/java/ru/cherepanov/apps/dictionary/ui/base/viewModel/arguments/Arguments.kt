package ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
sealed class Arguments {
    open fun encodeToString(): String {
        return ""
    }

    companion object {
        const val ARG_KEY = "args"

        inline fun <reified T : Arguments> decodeFromString(data: String): T {
            return Json.decodeFromString(data)
        }
    }
}