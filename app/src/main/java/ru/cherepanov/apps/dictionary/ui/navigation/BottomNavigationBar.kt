package ru.cherepanov.apps.dictionary.ui.navigation

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun BottomNavigationBar(
    currentRoute: String = "",
    onSwitchScreen: (Sections) -> Unit = {}
) {
    BottomAppBar {
        for (item in Sections) {
            NavigationBarItem(
                selected = item.route == currentRoute,
                onClick = { onSwitchScreen(item) },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = null)
                },
                label = { Text(text = stringResource(id = item.labelResId)) }
            )
        }
    }
}