package ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments

import android.util.Base64
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
abstract class Arguments {
    protected open fun encodeToString(): String {
        return ""
    }

    fun encodeToBase64String(): String {
        return Base64.encodeToString(encodeToString().toByteArray(), Base64.URL_SAFE)
    }

    companion object {
        const val ARG_KEY = "args"

        inline fun <reified T : Arguments> decodeFromString(data: String): T {
            return Json.decodeFromString(Base64.decode(data, Base64.URL_SAFE).decodeToString())
        }
    }
}