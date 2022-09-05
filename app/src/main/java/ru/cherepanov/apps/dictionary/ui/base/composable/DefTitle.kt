package ru.cherepanov.apps.dictionary.ui.base.composable

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import ru.cherepanov.apps.dictionary.R

@Composable
fun DefTitle(
    modifier: Modifier = Modifier,
    num: String?,
    title: String
) {
    Text(
        modifier = modifier,
        text = createIdTitle(num, title),
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun createIdTitle(
    num: String?,
    title: String
) = if (num == null) title else stringResource(id = R.string.num_title, num, title)