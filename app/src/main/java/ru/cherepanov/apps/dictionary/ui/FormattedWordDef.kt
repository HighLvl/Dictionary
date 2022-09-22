package ru.cherepanov.apps.dictionary.ui

import androidx.compose.ui.graphics.Color
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
    isDetails: Boolean = false,
    wordLinkColor: Color = Color.Unspecified
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
        gloss = formatValue(gloss, isDetails, wordLinkColor) ?: AnnotatedString(""),
        syllables = syllables,
        lang = lang,
        examplesText = examples.takeIfVisible()?.let {
            if (isDetails) formatFullDefExamples(examples, wordLinkColor)
            else formatShortDefExamples(examples)
        },
        synonyms = formatValue(synonyms, isDetails, wordLinkColor),
        antonyms = formatValue(antonyms, isDetails, wordLinkColor),
        hyponyms = formatValue(hyponyms, isDetails, wordLinkColor),
        hypernyms = formatValue(hypernyms, isDetails, wordLinkColor),
        etymology = formatValue(etymology, isDetails, wordLinkColor),
        phras = formatValue(phras?.filter { it.isNotBlank() }
            ?.joinToString("\n") { "\"${it}\"" }
            .takeIfVisible(), isDetails, wordLinkColor),
        ipa = formatValue(ipa, isDetails, wordLinkColor),
    )
}

private fun formatValue(value: String?, isDetails: Boolean, wordLinkColor: Color) =
    value.takeIfVisible()
        ?.replaceTagWithStyle(
            aFullTextReplaceData,
            if (isDetails) buildATitleFullDefReplaceData(wordLinkColor)
            else aTitleShortDefReplaceData
        )

private fun formatFullDefExamples(examples: List<String>, wordLinkColor: Color): AnnotatedString =
    buildAnnotatedString {
        examples.forEachIndexed { index, example ->
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("${index + 1}. ")
            }
            append(
                example.replaceTagWithStyle(
                    aFullTextReplaceData,
                    wTitleTagReplaceData,
                    buildATitleFullDefReplaceData(wordLinkColor)
                )
            )
            append('\n')
            withStyle(style = SpanStyle(fontSize = 4.sp)) {
                append('\n')
            }
        }
    }

private fun formatShortDefExamples(examples: List<String>): AnnotatedString = buildAnnotatedString {
    examples.forEachIndexed { index, example ->
        append(
            example.replaceTagWithStyle(
                aFullTextReplaceData,
                wTitleTagReplaceData,
                aTitleShortDefReplaceData
            )
        )
        if (index != examples.lastIndex) {
            append('\n')
        }
    }
}

private val aFullTextReplaceData = ReplaceData(
    "\\{a full_text=[^\\}]+\\}",
    "\\{/a\\}",
    SpanStyle(fontStyle = FontStyle.Italic)
)
private val wTitleTagReplaceData = ReplaceData(
    "\\{wtitle\\}",
    "\\{/wtitle\\}",
    SpanStyle(fontWeight = FontWeight.Bold)
)

private fun buildATitleFullDefReplaceData(wordLinkColor: Color) = ReplaceData(
    "\\{a title=([^\\}]+)\\}",
    "\\{/a\\}",
    SpanStyle(color = wordLinkColor),
    AnnotationData(groupNum = 0, tag = AnnotationTag.URI)
)

private val aTitleShortDefReplaceData = ReplaceData(
    "\\{a title=([^\\}]+)\\}",
    "\\{/a\\}"
)

private fun String.replaceTagWithStyle(
    vararg replaceDataItems: ReplaceData
) = buildAnnotatedString {
    val string = this@replaceTagWithStyle
    val tagDataList = string.obtainTagDataList(replaceDataItems)

    var startIndex = 0
    val openTagQueue = mutableListOf<TagData.Open>()
    for (tagData in tagDataList) {
        val endIndex = tagData.matchResult.range.first
        if (endIndex > 0) {
            val token = string.substring(startIndex, endIndex)
            append(token)
        }
        if (tagData is TagData.Open) {
            tagData.replaceData.annotationData?.let { annotationData ->
                pushStringAnnotation(
                    annotationData.tag.value,
                    tagData.matchResult.groupValues[annotationData.groupNum + 1]
                )
            }
            pushStyle(tagData.replaceData.style)
            openTagQueue.add(tagData)
        } else {
            pop()
            openTagQueue.removeLast().replaceData.annotationData?.let {
                pop()
            }
        }
        startIndex = tagData.matchResult.range.last + 1
    }


    if (startIndex < string.length) {
        append(substring(startIndex, string.length))
    }
}

private fun String.obtainTagDataList(replaceDataItems: Array<out ReplaceData>): Sequence<TagData> {
    val tagDataSequence: Sequence<TagDataSequence> =
        replaceDataItems.asSequence().distinctBy { it.openTag }.map {
            TagDataSequence.Open(
                replaceData = it,
                sequence = it.openTag.toRegex().findAll(this)
            )
        } + replaceDataItems.asSequence().distinctBy { it.closeTag }.map {
            TagDataSequence.Close(
                sequence = it.closeTag.toRegex().findAll(this)
            )
        }

    val tagDataList = tagDataSequence.map { tagDataSequenceItem ->
        tagDataSequenceItem.sequence.map { matchResult ->
            when (tagDataSequenceItem) {
                is TagDataSequence.Open -> TagData.Open(
                    tagDataSequenceItem.replaceData,
                    matchResult
                )
                is TagDataSequence.Close -> TagData.Close(matchResult)
            }
        }
    }.flatten().sortedBy { it.matchResult.range.first }
    return tagDataList
}

private fun List<String>?.takeIfVisible() = takeUnless { isNullOrEmpty() || all { it.isBlank() } }

private fun String?.takeIfVisible() = takeUnless { isNullOrBlank() }

private data class ReplaceData(
    val openTag: String,
    val closeTag: String,
    val style: SpanStyle = SpanStyle(),
    val annotationData: AnnotationData? = null
)

private data class AnnotationData(
    val groupNum: Int,
    val tag: AnnotationTag
)

enum class AnnotationTag(val value: String) {
    URI("URI")
}

private sealed class TagDataSequence(open val sequence: Sequence<MatchResult>) {
    data class Open(val replaceData: ReplaceData, override val sequence: Sequence<MatchResult>) :
        TagDataSequence(sequence)

    data class Close(override val sequence: Sequence<MatchResult>) : TagDataSequence(sequence)
}


private sealed class TagData(open val matchResult: MatchResult) {
    data class Open(val replaceData: ReplaceData, override val matchResult: MatchResult) :
        TagData(matchResult)

    data class Close(override val matchResult: MatchResult) : TagData(matchResult)
}

