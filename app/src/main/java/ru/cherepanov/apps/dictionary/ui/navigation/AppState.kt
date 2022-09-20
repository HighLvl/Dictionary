package ru.cherepanov.apps.dictionary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.ui.base.viewModel.arguments.Arguments
import ru.cherepanov.apps.dictionary.ui.details.DetailsArgs
import ru.cherepanov.apps.dictionary.ui.search.SearchArgs
import ru.cherepanov.apps.dictionary.ui.searchList.SearchListArgs


@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
    finish: () -> Unit
) = remember(navController) {
    AppState(navController, finish)
}

class AppState(
    val navController: NavHostController,
    val finish: () -> Unit
) {
    val currentRoute: String
        @Composable
        get() = navController.currentBackStackEntryAsState().value.currentSection.route

    fun switchScreen(section: Sections) {
        val currentSection = navController.currentBackStackEntry.currentSection
        if (currentSection == section) return
        when (section) {
            Sections.Main -> {
                navController.popBackStack()
            }
            Sections.Favorites -> {
                navController.navigate(section.route)
            }
        }
    }

    fun back() {
        if (!navController.popBackStack()) {
            finish()
        }
    }

    fun navigateToDetails(defId: DefId) {
        navController.navigate(Destinations.Details.getRoute(DetailsArgs(defId)))
    }

    fun navigateToSearchList(
        defId: DefId? = null
    ) {
        navController.navigate(Destinations.SearchList.getRoute(SearchListArgs(defId = defId)))
    }

    fun navigateToSearchList(title: String) {
        val prevBackStackEntryRoute = navController.previousBackStackEntry?.destination?.route
        val isSearchCalledBySearchList = prevBackStackEntryRoute == Destinations.SearchList.route
        if (isSearchCalledBySearchList) {
            navController.popBackStack(Destinations.SearchList.route, true)
        } else {
            navController.popBackStack()
        }
        navController.navigate(Destinations.SearchList, SearchListArgs(title = title)) {
            launchSingleTop = true
        }
    }

    fun navigateToSearchInitially(searchTerm: String?) {
        navController.popBackStack(Destinations.Home.route, true)
        if (searchTerm == null) {
            val route = Destinations.SearchList.getRoute(SearchListArgs())
            navController.navigate(route)
        } else {
            navigateToSearch(searchTerm)
        }
    }

    fun navigateToSearch(searchTerm: String? = null) {
        navController.navigate(Destinations.Search.getRoute(SearchArgs(searchTerm)))
    }

    fun isOnlyEntryInBackStack(backStackEntry: NavBackStackEntry): Boolean {
        return navController.backQueue
            .takeWhile { it != backStackEntry }
            .none { it.destination !is NavGraph }
    }

    private val NavBackStackEntry?.currentSection: Sections
        get() = this?.destination?.hierarchy?.first()?.parent?.route?.let {
            Sections.findScreenByRoute(it)
        } ?: Sections.Main

    private fun <T : Arguments> NavHostController.navigate(
        destination: Destinations<T>,
        args: T,
        navOptionsBuilder: NavOptionsBuilder.() -> Unit
    ) {
        navigate(destination.getRoute(args), navOptionsBuilder)
    }
}