package ru.cherepanov.apps.dictionary.ui.details

import androidx.annotation.StringRes
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.cherepanov.apps.dictionary.R
import ru.cherepanov.apps.dictionary.ui.base.FormattedWordDef
import ru.cherepanov.apps.dictionary.ui.base.composable.createTermToGlossString

@Composable
fun FormattedWordDef.getDetailsAnnotatedString() = buildAnnotatedString {
    spacing(with(LocalDensity.current) { 8.dp.toSp() })
    Language(lang)
    PartOfSpeech(pos)
    SyllablesToGloss(syllables, id.title, gloss)
    RelatedWords(
        synonyms,
        antonyms,
        hyponyms,
        hypernyms,
    )
    Examples(examplesText)
    Etymology(etymology)
    Phraseologisms(phras)
    Ipa(ipa)
    spacing(with(LocalDensity.current) { 80.dp.toSp() })
}

@Composable
private fun AnnotatedString.Builder.Ipa(ipa: AnnotatedString?) {
    KeyValueRow(
        R.string.ipa_label, ipa,
        keyColor = MaterialTheme.colorScheme.tertiary,
        keyStyle = MaterialTheme.typography.bodyMedium,
        valueStyle = MaterialTheme.typography.bodyMedium,
        valueColor = MaterialTheme.colorScheme.secondary,
        addSpacer = true
    )
}

@Composable
private fun AnnotatedString.Builder.Phraseologisms(phras: AnnotatedString?) {
    KeyValueColumn(
        R.string.phras_label,
        keyColor = MaterialTheme.colorScheme.tertiary,
        keyStyle = MaterialTheme.typography.bodyLarge,
        valueStyle = MaterialTheme.typography.bodyMedium,
        valueColor = MaterialTheme.colorScheme.secondary,
        value = phras,
        addSpacer = true
    )
}

@Composable
private fun AnnotatedString.Builder.Etymology(
    etymology: AnnotatedString?
) {
    etymology ?: return
    KeyValueColumn(
        R.string.etimology_label,
        keyStyle = MaterialTheme.typography.bodyLarge,
        keyColor = MaterialTheme.colorScheme.tertiary,
        value = etymology,
        addSpacer = true,
        valueStyle = MaterialTheme.typography.bodyMedium,
        capitalize = true
    )
}

@Composable
private fun AnnotatedString.Builder.PartOfSpeech(pos: AnnotatedString?) {
    KeyValueRow(
        R.string.pos_label,
        pos,
        keyStyle = MaterialTheme.typography.bodyMedium,
        addSpacer = false,
        keyColor = MaterialTheme.colorScheme.tertiary
    )
}

@Composable
private fun AnnotatedString.Builder.Language(lang: String) {
    KeyValueRow(
        R.string.language_label,
        AnnotatedString(lang),
        keyStyle = MaterialTheme.typography.bodyMedium,
        keyColor = MaterialTheme.colorScheme.tertiary
    )
}

@Composable
private fun AnnotatedString.Builder.RelatedWords(
    synonyms: AnnotatedString?,
    antonyms: AnnotatedString?,
    hyponyms: AnnotatedString?,
    hypernyms: AnnotatedString?,
) {
    if (synonyms != null || hyponyms != null || hypernyms != null || antonyms != null) {
        TextBlockSpacer()
    }

    KeyValueRow(
        R.string.full_syn_label,
        synonyms,
        keyColor = MaterialTheme.colorScheme.tertiary,
        keyStyle = MaterialTheme.typography.bodyMedium,
        valueStyle = MaterialTheme.typography.bodyMedium,
        valueColor = MaterialTheme.colorScheme.secondary
    )
    KeyValueRow(
        R.string.full_ant_label,
        antonyms,
        keyColor = MaterialTheme.colorScheme.tertiary,
        keyStyle = MaterialTheme.typography.bodyMedium,
        valueStyle = MaterialTheme.typography.bodyMedium,
        valueColor = MaterialTheme.colorScheme.secondary
    )
    KeyValueRow(
        R.string.full_hypo_label,
        hyponyms,
        keyColor = MaterialTheme.colorScheme.tertiary,
        keyStyle = MaterialTheme.typography.bodyMedium,
        valueStyle = MaterialTheme.typography.bodyMedium,
        valueColor = MaterialTheme.colorScheme.secondary
    )
    KeyValueRow(
        R.string.full_hyper_label,
        hypernyms,
        keyColor = MaterialTheme.colorScheme.tertiary,
        keyStyle = MaterialTheme.typography.bodyMedium,
        valueStyle = MaterialTheme.typography.bodyMedium,
        valueColor = MaterialTheme.colorScheme.secondary
    )
}

@Composable
private fun AnnotatedString.Builder.SyllablesToGloss(
    syllables: String,
    title: String,
    gloss: AnnotatedString
) {
    TextBlockSpacer()
    withStyle(
        MaterialTheme.typography.bodyMedium
            .copy(color = MaterialTheme.colorScheme.onBackground)
            .toSpanStyle()
    ) {
        append(
            createTermToGlossString(
                term = syllables.ifBlank { title },
                gloss = gloss
            )
        )
        append('\n')
    }
}


@Composable
private fun AnnotatedString.Builder.KeyValueRow(
    @StringRes keyResId: Int = R.string.full_ant_label,
    value: AnnotatedString?,
    keyStyle: TextStyle = LocalTextStyle.current,
    keyColor: Color = Color.Unspecified,
    valueStyle: TextStyle = LocalTextStyle.current,
    valueColor: Color = MaterialTheme.colorScheme.onBackground,
    addSpacer: Boolean = false,
    capitalize: Boolean = true
) {
    value ?: return
    if (addSpacer) TextBlockSpacer()
    withStyle(keyStyle.copy(color = keyColor).toSpanStyle()) {
        append(stringResource(id = keyResId).let {
            if (capitalize) it.capitalize(Locale.current)
            else it
        })
    }
    withStyle(valueStyle.copy(color = valueColor).toSpanStyle()) {
        append(value)
    }
    append('\n')
}

@Composable
private fun AnnotatedString.Builder.Examples(examples: AnnotatedString?) {
    examples ?: return
    TextBlockSpacer()
    withStyle(
        MaterialTheme.typography.bodyLarge
            .copy(color = MaterialTheme.colorScheme.tertiary)
            .toSpanStyle()
    ) {
        append(stringResource(id = R.string.examples_label).capitalize(Locale.current))
        append('\n')
    }

    spacing(8.sp)

    withStyle(
        MaterialTheme.typography.bodyMedium
            .copy(
                color = MaterialTheme.colorScheme.onBackground
            ).toSpanStyle()
    ) {
        append(examples)
    }
}

private fun AnnotatedString.Builder.spacing(height: TextUnit = 4.sp) {
    withStyle(SpanStyle(fontSize = height)) {
        append('\n')
    }
}

@Composable
private fun AnnotatedString.Builder.KeyValueColumn(
    @StringRes keyResId: Int = R.string.full_ant_label,
    keyStyle: TextStyle = LocalTextStyle.current,
    keyColor: Color = Color.Unspecified,
    valueStyle: TextStyle = LocalTextStyle.current,
    valueColor: Color = MaterialTheme.colorScheme.onBackground,
    value: AnnotatedString?,
    addSpacer: Boolean = false,
    keySpaceHeight: TextUnit = 8.sp,
    capitalize: Boolean = true
) {
    value ?: return
    if (addSpacer) TextBlockSpacer()
    withStyle(keyStyle.copy(color = keyColor).toSpanStyle()) {
        append(stringResource(id = keyResId).let {
            if (capitalize) it.capitalize(Locale.current)
            else it
        })
        append('\n')
    }
    spacing(keySpaceHeight)
    withStyle(valueStyle.toSpanStyle().copy(color = valueColor)) {
        append(value)
        append('\n')
    }
}

@Composable
private fun AnnotatedString.Builder.TextBlockSpacer() {
    append("\n")
}
