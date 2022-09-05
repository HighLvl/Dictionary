package ru.cherepanov.apps.dictionary.domain.model

data class WordDef(
    val id: DefId = DefId(),
    val syllables: String = "tit-le",
    val pos: String = "noun",
    val gloss: String = "some gloss",
    val examples: List<String> = listOf("Add a title to your slides"),
    val synonyms: String = "header",
    val antonyms: String = "-",
    val hyponyms: String? = "-",
    val hypernyms: String? = "-",
    val etymology: String? = "",
    val phras: List<String>? = listOf("ffdff", "ffdgg"),
    val ipa: String? = "",
    val isFavorite: Boolean = false,
    val lang: String = "English",
    val isFull: Boolean = true
)
