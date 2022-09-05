package ru.cherepanov.apps.dictionary.ui.base.composable

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Language(modifier: Modifier = Modifier, lang: String) {
    Text(
        modifier = modifier,
        text = lang,
        style = MaterialTheme.typography.titleSmall
    )
}