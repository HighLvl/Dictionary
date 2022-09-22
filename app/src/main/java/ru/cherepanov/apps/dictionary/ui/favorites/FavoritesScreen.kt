package ru.cherepanov.apps.dictionary.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.paging.map
import kotlinx.coroutines.flow.map
import ru.cherepanov.apps.dictionary.R
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.domain.model.WordDef
import ru.cherepanov.apps.dictionary.ui.FormattedWordDef
import ru.cherepanov.apps.dictionary.ui.base.composable.*
import ru.cherepanov.apps.dictionary.ui.toFormatted


@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    onItemSelected: (DefId) -> Unit = {},
    onBackPressed: (() -> Unit)
) {
    FavoritesScreen(
        modifier = modifier,
        viewModel = hiltViewModel(),
        onItemSelected = onItemSelected,
        onBackPressed = onBackPressed
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesScreen(
    modifier: Modifier,
    viewModel: FavoritesViewModel,
    onItemSelected: (DefId) -> Unit,
    onBackPressed: (() -> Unit)
) {
    val lazyPagingItems = viewModel.pagingData.map {
        it.map(WordDef::toFormatted)
    }.collectAsLazyPagingItems()

    Scaffold(
        modifier = modifier,
        topBar = {
            TitleTopBar(
                titleResId = R.string.favorites_label,
                navigationIcon = {
                    BackButton(onBackPressed = onBackPressed)
                }
            )
        }
    ) {
        FavoritesContent(
            modifier = Modifier.padding(it),
            lazyPagingItems = lazyPagingItems,
            onRemoveFromFavorites = viewModel::onRemoveFromFavorites,
            onClick = { id ->
                onItemSelected(id)
            }
        )
    }
}


@Composable
private fun FavoritesContent(
    modifier: Modifier = Modifier,
    lazyPagingItems: LazyPagingItems<FormattedWordDef>,
    onRemoveFromFavorites: (DefId) -> Unit,
    onClick: (DefId) -> Unit = {},
) {
    DefList(
        modifier = modifier,
        contentPadding = PaddingValues(top = 8.dp, bottom = 0.dp),
        scrollState = lazyPagingItems.rememberLazyListState()
    ) {
        if (lazyPagingItems.loadState.prepend == LoadState.Loading) {
            item {
                LoadingItem()
            }
        }

        items(
            items = lazyPagingItems,
            key = { it.id }
        ) { favoriteItem ->
            if (favoriteItem == null) {
                LoadingItem()
            } else {
                SwipeToRemove(onRemove = { onRemoveFromFavorites(favoriteItem.id) }) {
                    FavoriteItem(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        wordDef = favoriteItem,
                        onClick = { onClick(favoriteItem.id) }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (lazyPagingItems.loadState.refresh == LoadState.Loading) {
            item {
                ProgressBar(modifier = Modifier.fillParentMaxSize())
            }
        }
        if (lazyPagingItems.loadState.append == LoadState.Loading) {
            item {
                LoadingItem()
            }
        }
    }
}


@Composable
fun <T : Any> LazyPagingItems<T>.rememberLazyListState(): LazyListState {
    // After recreation, LazyPagingItems first return 0 items, then the cached items.
    // This behavior/issue is resetting the LazyListState scroll position.
    // Below is a workaround. More info: https://issuetracker.google.com/issues/177245496.
    return when (itemCount) {
        // Return a different LazyListState instance.
        0 -> remember(this) { LazyListState(0, 0) }
        // Return rememberLazyListState (normal case).
        else -> androidx.compose.foundation.lazy.rememberLazyListState()
    }
}

@Composable
private fun LoadingItem() {
    ProgressBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 16.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteItem(
    modifier: Modifier = Modifier,
    wordDef: FormattedWordDef,
    onClick: () -> Unit = {}
) = with(wordDef) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = createTermToGlossString(
                    createIdTitle(
                        num = num,
                        title = id.title
                    ),
                    gloss
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Language(lang = lang)
        }
    }
}