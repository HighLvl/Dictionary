package ru.cherepanov.apps.dictionary.domain.model

data class WordDef(
    val id: DefId,
    val syllables: String = "",
    val pos: String = "",
    val gloss: String = "",
    val examples: List<String> = emptyList(),
    val synonyms: String = "",
    val antonyms: String = "",
    val hyponyms: String? = null,
    val hypernyms: String? = null,
    val etymology: String? = null,
    val phras: List<String>? = null,
    val ipa: String? = null,
    val isFavorite: Boolean = false,
    val lang: String = "",
    val isFull: Boolean = false
)
