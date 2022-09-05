package ru.cherepanov.apps.dictionary.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDef

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

fun WordDefDto.mapToFullDef(): WordDef {
    return WordDef(
        id = DefId(
            title = title,
            langNum = langNum,
            senseNum = senseNum,
            glossNum = glossNum
        ),
        syllables = syllables,
        gloss = gloss,
        pos = pos,
        examples = examples,
        synonyms = synonyms,
        antonyms = antonyms,
        hypernyms = hypernyms,
        hyponyms = hyponyms,
        etymology = etymology,
        phras = phras,
        ipa = ipa,
        lang = lang,
        isFavorite = false,
        isFull = true
    )
}

fun WordDefDto.mapToShortDef(): WordDef {
    return WordDef(
        id = DefId(
            title = title,
            langNum = langNum,
            senseNum = senseNum,
            glossNum = glossNum
        ),
        syllables = syllables,
        gloss = gloss,
        pos = pos,
        examples = examples,
        synonyms = synonyms,
        antonyms = antonyms,
        lang = lang,
        isFavorite = false,
        isFull = false
    )
}