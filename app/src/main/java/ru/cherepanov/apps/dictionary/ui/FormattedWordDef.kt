package ru.cherepanov.apps.dictionary.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import ru.cherepanov.apps.dictionary.ui.base.RomanNumeral

data class FormattedWordDef(
    val id: DefId,
    val title: String,
    val isFull: Boolean,
    val isFavorite: Boolean,
    val num: String?,
    val numVisibility: Boolean,
    val pos: String,
    val posVisibility: Boolean,
    val gloss: AnnotatedString,
    val syllables: String,
    val lang: String,
    val examplesText: AnnotatedString,
    val examplesVisibility: Boolean,
    val synonyms: AnnotatedString,
    val synonymsVisibility: Boolean,
    val antonyms: AnnotatedString,
    val antonymsVisibility: Boolean,
    val hyponyms: AnnotatedString?,
    val hyponymsVisibility: Boolean,
    val hypernyms: AnnotatedString?,
    val hypernymsVisibility: Boolean,
    val etymology: AnnotatedString?,
    val etymologyVisibility: Boolean,
    val phras: String?,
    val phrasVisibility: Boolean,
    val ipa: AnnotatedString?,
    val ipaVisibility: Boolean
)

fun WordDef.toFormatted(
    useAbbr: Boolean = true,
    details: Boolean = false
): FormattedWordDef {
    val senseNum = id.senseNum?.plus(1)?.let(RomanNumeral::value)?.plus(".")
    val glossNum = id.glossNum?.plus(1)?.toString()?.plus(".")
    val num = listOfNotNull(senseNum, glossNum).joinToString("").ifEmpty { null }

    return FormattedWordDef(
        id = id,
        title = id.title,
        isFull = isFull,
        isFavorite = isFavorite,
        num = num,
        numVisibility = isVisible(num),
        pos = pos,
        posVisibility = isVisible(pos),
        gloss = formatValue(gloss, useAbbr),
        syllables = syllables,
        lang = lang,
        examplesText = if (details) formatFullDefExamples(examples, useAbbr)
        else formatShortDefExamples(examples),
        examplesVisibility = isVisible(examples),
        synonyms = formatValue(synonyms, useAbbr),
        synonymsVisibility = isVisible(synonyms),
        antonyms = formatValue(antonyms, useAbbr),
        antonymsVisibility = isVisible(antonyms),
        hyponyms = hyponyms?.let { formatValue(it, useAbbr) },
        hyponymsVisibility = isVisible(hyponyms),
        hypernyms = hypernyms?.let { formatValue(it, useAbbr) },
        hypernymsVisibility = isVisible(hypernyms),
        etymology = etymology?.let { formatValue(it, useAbbr) },
        etymologyVisibility = isVisible(etymology),
        phras = phras?.filter { it.isNotBlank() }
            ?.joinToString("\n") { "\"${it}\"" },
        phrasVisibility = isVisible(phras),
        ipa = ipa?.let { formatValue(it, useAbbr) },
        ipaVisibility = isVisible(ipa)
    )
}

private fun formatValue(value: String, useAbbr: Boolean) =
    value.replaceTagWithStyle(
        "\\{a full_text=[^\\}]+\\}",
        "\\{/a\\}",
        SpanStyle(fontStyle = FontStyle.Italic)
    )


private fun formatFullDefExamples(examples: List<String>, useAbbr: Boolean): AnnotatedString =
    buildAnnotatedString {
        examples.forEachIndexed { index, example ->
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("${index + 1}. ")
            }
            append(
                example.replaceWtitleTagWithBold()
            )
            append('\n')
            withStyle(style = SpanStyle(fontSize = 4.sp)) {
                append('\n')
            }
        }
    }

private fun formatShortDefExamples(examples: List<String>): AnnotatedString = buildAnnotatedString {
    examples.forEachIndexed { index, example ->
        append(example.replaceWtitleTagWithBold())
        if (index != examples.lastIndex) {
            append('\n')
        }
    }
}

private fun String.replaceWtitleTagWithBold() =
    replaceTagWithStyle(
        "\\{wtitle\\}",
        "\\{/wtitle\\}",
        SpanStyle(fontWeight = FontWeight.Bold)
    )


private fun String.replaceTagWithStyle(
    openTag: String,
    closeTag: String,
    style: SpanStyle
) = buildAnnotatedString {
    val string = this@replaceTagWithStyle
    val searchRegex = "($openTag)|($closeTag)".toRegex()

    val tagMatches = searchRegex.findAll(string)

    var startIndex = 0
    for (matchResult in tagMatches) {
        val endIndex = matchResult.range.first
        if (endIndex > 0) {
            val token = string.substring(startIndex, endIndex)
            append(token)
        }
        val isOpenTag = matchResult.groupValues[1].isNotEmpty()
        if (isOpenTag) {
            pushStyle(style)
        } else {
            pop()
        }
        startIndex = matchResult.range.last + 1
    }

    if (startIndex < string.length) {
        append(string.substring(startIndex, string.length))
    }
}

private fun isVisible(list: List<String>?) = !(list.isNullOrEmpty() || list.all { it.isBlank() })

private fun isVisible(string: String?) = !string.isNullOrBlank()
