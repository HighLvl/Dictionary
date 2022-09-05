package ru.cherepanov.apps.dictionary.ui.navigation

import ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments.Arguments
import ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments.Arguments.Companion.ARG_KEY
import ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments.NoArgs
import ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments.DetailsArgs
import ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments.SearchArgs
import ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments.SearchListArgs


sealed class Destinations<T : Arguments>(
    private val root: String
) {
    val route = "$root/{$ARG_KEY}"

    fun getRoute(args: T? = null): String {
        val nullSafeArgs = args ?: NoArgs
        return "$root/${nullSafeArgs.encodeToString()}"
    }

    object Search : Destinations<SearchArgs>(root = "search")
    object SearchList : Destinations<SearchListArgs>(root = "searchList")
    object Favorites : Destinations<NoArgs>(root = "favoritesList")
    object Details : Destinations<DetailsArgs>(root = "details")
}