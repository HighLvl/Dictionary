package ru.cherepanov.apps.dictionary.ui.base.composable

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import ru.cherepanov.apps.dictionary.R

@Composable
fun FavoriteIcon(isFavorite: Boolean) {
    Icon(
        tint = if (isFavorite) MaterialTheme.colorScheme.secondary
        else Color.Unspecified,
        painter = painterResource(
            id = if (isFavorite) R.drawable.ic_favorite
            else R.drawable.ic_favorite_border_black
        ),
        contentDescription = null
    )
}