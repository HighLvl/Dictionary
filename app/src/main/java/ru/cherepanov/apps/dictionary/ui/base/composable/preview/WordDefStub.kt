package ru.cherepanov.apps.dictionary.ui.base.composable.preview

import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import ru.cherepanov.apps.dictionary.ui.FormattedWordDef
import ru.cherepanov.apps.dictionary.ui.toFormatted

fun wordDefStub(): WordDef {
    return WordDef(
        id = DefId(title = "title", langNum = 0, senseNum = 0, glossNum = 0),
        syllables = "syl-lab-les",
        antonyms = "antonyms",
        synonyms = (1..50).fold("") {prev, _ -> prev + "synonyms"},
        examples = listOf("examples"),
        gloss = "gloss",
        isFavorite = false,
        isFull = false,
        lang = "lang",
        pos = "part of speech"
    )
}

fun formattedWordDefStub(): FormattedWordDef = wordDefStub().toFormatted()