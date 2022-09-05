package ru.cherepanov.apps.dictionary.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class DefId(
    val title: String,
    val langNum: Int?,
    val senseNum: Int?,
    val glossNum: Int?,
): Parcelable
