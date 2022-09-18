package ru.cherepanov.apps.dictionary.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.cherepanov.apps.dictionary.domain.model.WordDefRemoteData

@Serializable
data class WordDefDto(
    @SerialName("title")
    val title: String,
    @SerialName("syllables")
    val syllables: String,
    @SerialName("lang")
    val lang: String,
    @SerialName("lang_num")
    val langNum: Int? = null,
    @SerialName("sense_num")
    val senseNum: Int? = null,
    @SerialName("gloss_num")
    val glossNum: Int? = null,
    @SerialName("pos")
    val pos: String,
    @SerialName("gloss")
    val gloss: String,
    @SerialName("examples")
    val examples: List<String>,
    @SerialName("synonyms")
    val synonyms: String,
    @SerialName("antonyms")
    val antonyms: String,

    @SerialName("hyponyms")
    val hyponyms: String? = null,
    @SerialName("hypernyms")
    val hypernyms: String? = null,
    @SerialName("etimology")
    val etymology: String? = null,
    @SerialName("phras")
    val phras: List<String>? = null,
    @SerialName("ipa")
    val ipa: String? = null
)

fun WordDefDto.mapToWordDefRemoteData(): WordDefRemoteData =
    WordDefRemoteData(
        title = title,
        syllables = syllables,
        lang = lang,
        langNum = langNum,
        senseNum = senseNum,
        glossNum = glossNum,
        pos = pos,
        gloss = gloss,
        examples = examples,
        synonyms = synonyms,
        antonyms = antonyms,
        hyponyms = hyponyms,
        hypernyms = hypernyms,
        etymology = etymology,
        phras = phras,
        ipa = ipa
    )