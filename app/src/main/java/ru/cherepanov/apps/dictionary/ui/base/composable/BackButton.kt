package ru.cherepanov.apps.dictionary.ui.base.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BackButton(modifier: Modifier = Modifier, onBackPressed: () -> Unit) {
    IconButton(
        modifier = modifier.padding(start = 8.dp, end = 8.dp),
        onClick = onBackPressed
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = null
        )
    }
}