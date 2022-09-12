package ru.cherepanov.apps.dictionary.ui.base.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.cherepanov.apps.dictionary.R

@Composable
fun LoadingError(modifier: Modifier = Modifier, retry: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.loading_error_msg))
        TextButton(onClick = retry) {
            Text(text = stringResource(R.string.refresh_msg))
        }
    }
}