package ru.cherepanov.apps.dictionary.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import ru.cherepanov.apps.dictionary.R

enum class Sections(val route: String, @StringRes val labelResId: Int, val icon: ImageVector) {
    Main("main", R.string.search_label, Icons.Filled.Search),
    Favorites("favorites", R.string.favorites_label, Icons.Filled.Favorite);

    companion object : List<Sections> by values().asList() {
        fun findScreenByRoute(route: String) = values().first { it.route == route }
    }
}