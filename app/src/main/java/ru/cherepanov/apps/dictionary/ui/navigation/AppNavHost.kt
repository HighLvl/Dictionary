package ru.cherepanov.apps.dictionary.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.ui.details.DefDetailsScreen
import ru.cherepanov.apps.dictionary.ui.favorites.FavoritesScreen
import ru.cherepanov.apps.dictionary.ui.search.SearchScreen
import ru.cherepanov.apps.dictionary.ui.searchList.SearchListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    args: Args? = null,
    onArgsProcessed: () -> Unit,
    appState: AppState,
    activityViewModelStoreOwner: ViewModelStoreOwner
) {
    if (args != null) {
        onArgsProcessed()
    }
    if (args != null && !args.isActivityArg) {
        LaunchedEffect(Unit) {
            appState.navigateToSearch(searchTerm = args.searchTerm)
        }
    }
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = appState.currentRoute,
                onSwitchScreen = appState::switchScreen
            )
        }
    ) {
        NavHost(
            navController = appState.navController,
            startDestination = Sections.Main.route
        ) {
            mainGraph(
                contentPadding = it,
                onSelectShortDef = appState::navigateToDetails,
                onBackPressed = appState::back,
                navigateToSearch = appState::navigateToSearchInitially,
                searchTerm = args?.takeIf { it.isActivityArg }?.searchTerm,
                onSelectWordSuggestion = { appState.navigateToSearchList(it) },
                onShowSearch = appState::navigateToSearch,
                isBackButtonGone = appState::isOnlyEntryInBackStack,
                provideSearchViewModelStoreOwner =  { activityViewModelStoreOwner }
            )
            favoritesGraph(
                contentPadding = it,
                onItemSelected = { appState.navigateToSearchList(defId = it) },
                onBackPressed = appState::back
            )
        }
    }
}

data class Args(val searchTerm: String, val isActivityArg: Boolean = false)

private fun NavGraphBuilder.mainGraph(
    contentPadding: PaddingValues,
    onSelectShortDef: (DefId) -> Unit,
    onBackPressed: () -> Unit,
    navigateToSearch: (String?) -> Unit,
    searchTerm: String?,
    onSelectWordSuggestion: (String) -> Unit,
    onShowSearch: () -> Unit,
    isBackButtonGone: (NavBackStackEntry) -> Boolean,
    provideSearchViewModelStoreOwner: () -> ViewModelStoreOwner
) {
    navigation(startDestination = Destinations.Home.route, Sections.Main.route) {
        addHomeDestination(navigateToSearch, searchTerm)
        addSearchListDestination(
            contentPadding,
            onSelectShortDef,
            onBackPressed,
            onShowSearch,
            isBackButtonGone
        )
        addDetailsDestination(contentPadding, onBackPressed)
        addSearchDestination(
            contentPadding,
            onBackPressed,
            onSelectWordSuggestion,
            provideSearchViewModelStoreOwner
        )
    }
}

private fun NavGraphBuilder.addHomeDestination(
    navigateToSearch: (String?) -> Unit,
    searchTerm: String?
) {
    composable(Destinations.Home.route) {
        LaunchedEffect(Unit) {
            navigateToSearch(searchTerm)
        }
    }
}

private fun NavGraphBuilder.addDetailsDestination(
    contentPadding: PaddingValues,
    onBackPressed: () -> Unit
) {
    composable(Destinations.Details.route) {
        DefDetailsScreen(
            modifier = Modifier.padding(contentPadding),
            onBackPressed = onBackPressed
        )
    }
}

private fun NavGraphBuilder.addSearchListDestination(
    contentPadding: PaddingValues,
    onSelectShortDef: (DefId) -> Unit,
    onBackPressed: () -> Unit,
    onShowSearch: () -> Unit,
    isBackButtonGone: (NavBackStackEntry) -> Boolean
) {
    composable(Destinations.SearchList.route) {
        SearchListScreen(
            modifier = Modifier.padding(contentPadding),
            onSelectShortDef = onSelectShortDef,
            onBackPressed = onBackPressed.takeUnless { _ -> isBackButtonGone(it) },
            onShowSearch = onShowSearch
        )
    }
}

private fun NavGraphBuilder.addSearchDestination(
    contentPadding: PaddingValues,
    onBackPressed: () -> Unit,
    onSelectSelectSuggestion: (String) -> Unit,
    provideSearchViewModelStoreOwner: () -> ViewModelStoreOwner
) {
    composable(route = Destinations.Search.route) {
        SearchScreen(
            modifier = Modifier.padding(contentPadding),
            arguments = it.arguments,
            onBackPressed = onBackPressed,
            onSelectSuggestion = onSelectSelectSuggestion,
            viewModelStoreOwner = remember {
                provideSearchViewModelStoreOwner()
            }
        )
    }
}

private fun NavGraphBuilder.favoritesGraph(
    contentPadding: PaddingValues,
    onItemSelected: (DefId) -> Unit,
    onBackPressed: () -> Unit
) {
    navigation(Destinations.Favorites.route, Sections.Favorites.route) {
        composable(Destinations.Favorites.route) {
            FavoritesScreen(
                modifier = Modifier.padding(contentPadding),
                onItemSelected = onItemSelected,
                onBackPressed = onBackPressed
            )
        }
    }
}