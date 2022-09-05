package ru.cherepanov.apps.dictionary.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class DefId(
    val title: String = "title",
    val langNum: Int? = 0,
    val senseNum: Int? = 0,
    val glossNum: Int? = 0,
): Parcelable
