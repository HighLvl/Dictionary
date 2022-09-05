package ru.cherepanov.apps.dictionary.ui.base.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleTopBar(@StringRes titleResId: Int, navigationIcon: @Composable () -> Unit = {}) {
    Column {
        SmallTopAppBar(
            title = {
                Text(text = stringResource(id = titleResId))
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            navigationIcon = navigationIcon
        )
        Divider(thickness = 1.dp)
    }
}