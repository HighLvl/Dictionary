package ru.cherepanov.apps.dictionary.domain.model


data class WordDefRemoteData(
    val title: String,
    val syllables: String,
    val lang: String,
    val langNum: Int? = null,
    val senseNum: Int? = null,
    val glossNum: Int? = null,
    val pos: String,
    val gloss: String,
    val examples: List<String>,
    val synonyms: String,
    val antonyms: String,
    val hyponyms: String? = null,
    val hypernyms: String? = null,
    val etymology: String? = null,
    val phras: List<String>? = null,
    val ipa: String? = null
)

fun WordDefRemoteData.mapToWordDef(isFull: Boolean = false, isFavorite: Boolean = false): WordDef {
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
        isFavorite = isFavorite,
        isFull = isFull
    )
}