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
    val pos: AnnotatedString?,
    val gloss: AnnotatedString,
    val syllables: String,
    val lang: String,
    val examplesText: AnnotatedString?,
    val synonyms: AnnotatedString?,
    val antonyms: AnnotatedString?,
    val hyponyms: AnnotatedString?,
    val hypernyms: AnnotatedString?,
    val etymology: AnnotatedString?,
    val phras: AnnotatedString?,
    val ipa: AnnotatedString?,
)

fun WordDef.toFormatted(
    useAbbr: Boolean = true,
    isDetails: Boolean = false
): FormattedWordDef {
    val senseNum = id.senseNum?.plus(1)?.let(RomanNumeral::value)?.plus(".")
    val glossNum = id.glossNum?.plus(1)?.toString()?.plus(".")
    val num = listOfNotNull(senseNum, glossNum).joinToString("").ifEmpty { null }

    return FormattedWordDef(
        id = id,
        title = id.title,
        isFull = isFull,
        isFavorite = isFavorite,
        num = num.takeIfVisible(),
        pos = pos.takeIfVisible()?.let { AnnotatedString(it) },
        gloss = formatValue(gloss, useAbbr) ?: AnnotatedString(""),
        syllables = syllables,
        lang = lang,
        examplesText = examples.takeIfVisible()?.let {
            if (isDetails) formatFullDefExamples(examples, useAbbr)
            else formatShortDefExamples(examples)
        },
        synonyms = formatValue(synonyms, useAbbr),
        antonyms = formatValue(antonyms, useAbbr),
        hyponyms = formatValue(hyponyms, useAbbr),
        hypernyms = formatValue(hypernyms, useAbbr),
        etymology = formatValue(etymology, useAbbr),
        phras = phras?.filter { it.isNotBlank() }
            ?.joinToString("\n") { "\"${it}\"" }
            .takeIfVisible()?.let { AnnotatedString(it) },
        ipa = formatValue(ipa, useAbbr),
    )
}

private fun formatValue(value: String?, useAbbr: Boolean) =
    value.takeIfVisible()?.replaceTagWithStyle(
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

private fun List<String>?.takeIfVisible() = takeUnless { isNullOrEmpty() || all { it.isBlank() } }

private fun String?.takeIfVisible() = takeUnless { isNullOrBlank() }
