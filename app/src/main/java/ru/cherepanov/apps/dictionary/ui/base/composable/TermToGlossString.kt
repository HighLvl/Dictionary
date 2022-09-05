package ru.cherepanov.apps.dictionary.ui.base.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import ru.cherepanov.apps.dictionary.R

@Composable
fun createTermToGlossString(term: String, gloss: AnnotatedString): AnnotatedString =
    buildAnnotatedString {
        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
        append(term)
        pop()
        append(stringResource(id = R.string.dash_gloss))
        append(gloss)
    }