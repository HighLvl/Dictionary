package ru.cherepanov.apps.dictionary.ui.base.composable

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import ru.cherepanov.apps.dictionary.R
import ru.cherepanov.apps.dictionary.ui.base.composable.theme.favoriteIconColorRes

@Composable
@Preview
private fun FavoriteIconPreview() {
    FavoriteIcon(isFavorite = true)
}

@Composable
fun FavoriteIcon(isFavorite: Boolean) {
    Icon(
        tint = if (isFavorite) colorResource(id = favoriteIconColorRes) else
            Color.Unspecified,
        painter = painterResource(
            id = if (isFavorite) R.drawable.ic_favorite
            else R.drawable.ic_favorite_border_black
        ),
        contentDescription = null
    )
}