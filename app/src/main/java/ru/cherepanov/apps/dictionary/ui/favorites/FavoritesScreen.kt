package ru.cherepanov.apps.dictionary.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import ru.cherepanov.apps.dictionary.R
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.ui.FormattedWordDef
import ru.cherepanov.apps.dictionary.ui.base.composable.*


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
    val lazyShortDefItems = viewModel.pagingData.collectAsLazyPagingItems()

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
            lazyShortDefItems = lazyShortDefItems,
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
    lazyShortDefItems: LazyPagingItems<FormattedWordDef>,
    onRemoveFromFavorites: (DefId) -> Unit,
    onClick: (DefId) -> Unit = {},
) {
    DefList(
        modifier = modifier,
        contentPadding = PaddingValues(top = 8.dp, bottom = 0.dp),
    ) {
        items(lazyShortDefItems) {
            if (it == null) return@items
            SwipeToRemove(onRemove = { onRemoveFromFavorites(it.id) }) {
                FavoriteItem(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                    shortDef = it,
                    onClick = { onClick(it.id) }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteItem(
    modifier: Modifier = Modifier,
    shortDef: FormattedWordDef,
    onClick: () -> Unit = {}
) = with(shortDef) {
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