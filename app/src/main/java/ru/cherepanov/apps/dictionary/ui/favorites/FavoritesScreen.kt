package ru.cherepanov.apps.dictionary.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.cherepanov.apps.dictionary.R
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.ui.FormattedWordDef
import ru.cherepanov.apps.dictionary.ui.base.viewModel.Resource
import ru.cherepanov.apps.dictionary.ui.base.composable.*


@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    onItemSelected: (DefId) -> Unit = {}
) {
    FavoritesScreen(
        modifier = modifier,
        viewModel = hiltViewModel(),
        onItemSelected = onItemSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesScreen(
    modifier: Modifier,
    viewModel: FavoritesViewModel,
    onItemSelected: (DefId) -> Unit
) {
    val resource by viewModel.uiState.observeAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TitleTopBar(titleResId = R.string.favorites_label)
        }
    ) {
        FavoritesContent(
            modifier = Modifier.padding(it),
            uiStateResource = resource!!,
            onRemoveFromFavorites = viewModel::onRemoveFromFavorites
        ) { id ->
            onItemSelected(id)
        }
    }
}

@Composable
private fun FavoritesContent(
    modifier: Modifier,
    uiStateResource: Resource<List<FormattedWordDef>>,
    onRemoveFromFavorites: (DefId) -> Unit,
    onClick: (DefId) -> Unit = {},
) {
    if (uiStateResource.isLoading()) return
    DefList(
        modifier = modifier,
        contentPadding = PaddingValues(top = 8.dp, bottom = 0.dp),
        shortDefs = uiStateResource.data
    ) {
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