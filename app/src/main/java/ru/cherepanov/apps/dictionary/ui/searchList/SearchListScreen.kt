package ru.cherepanov.apps.dictionary.ui.searchList

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.cherepanov.apps.dictionary.R
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.ui.base.TOP_APPBAR_HEIGHT
import ru.cherepanov.apps.dictionary.ui.base.composable.*
import ru.cherepanov.apps.dictionary.ui.base.observeUiState

@Composable
fun SearchListScreen(
    modifier: Modifier = Modifier,
    onSelectShortDef: (DefId) -> Unit = {},
    onBackPressed: (() -> Unit)? = null,
    onShowSearch: (String) -> Unit
) {
    SearchListScreen(
        modifier = modifier,
        viewModel = hiltViewModel(),
        onBackPressed = onBackPressed,
        onSelectShortDef = onSelectShortDef,
        onShowSearch = onShowSearch
    )
}

@Composable
private fun SearchListScreen(
    modifier: Modifier,
    viewModel: SearchListViewModel,
    onBackPressed: (() -> Unit)?,
    onSelectShortDef: (DefId) -> Unit,
    onShowSearch: (String) -> Unit
) {
    val uiState by viewModel.uiState.observeUiState()
    SearchListScreen(
        modifier = modifier,
        uiState = uiState,
        onBackPressed = onBackPressed,
        onSelectShortDef = onSelectShortDef,
        onLoadRandomWord = viewModel::onLoadRandomWord,
        onShowSearch = { onShowSearch(uiState.title) },
        onAddToFavorites = viewModel::onAddToFavorites,
        onRemoveFromFavorites = viewModel::onRemoveFromFavorites,
        onRetry = viewModel::retry
    )
}

@Composable
private fun SearchListScreen(
    modifier: Modifier,
    uiState: SearchListState,
    onBackPressed: (() -> Unit)?,
    onSelectShortDef: (DefId) -> Unit,
    onLoadRandomWord: () -> Unit,
    onShowSearch: () -> Unit,
    onAddToFavorites: (DefId) -> Unit,
    onRemoveFromFavorites: (DefId) -> Unit,
    onRetry: () -> Unit
) {
    val scrollState = rememberLazyListState()
    val collapsingToolbarState = rememberToolbarState(
        height = with(LocalDensity.current) {
            TOP_APPBAR_HEIGHT.dp.toPx()
        }
    )
    val scrollConnection = rememberCollapsingToolbarConnection(
        toolbarState = collapsingToolbarState,
        scrollableState = scrollState
    )

    StatusScaffold(
        modifier = modifier,
        status = uiState.status,
        floatingActionButton = {
            RandomWordFloatingButton(
                modifier = Modifier.padding(bottom = 8.dp, end = 16.dp),
                onClick = {
                    onLoadRandomWord()
                    collapsingToolbarState.expand()
                }
            )
        },
        topBar = {
            TitleTopBar(
                titleResId = R.string.search_label,
                navigationIcon = {
                    onBackPressed?.let {
                        BackButton(onBackPressed = it)
                    }
                }
            )
        },
        toolBar = {
            CollapsingToolbar(
                toolbarState = collapsingToolbarState,
                toolBar = {
                    SearchBar(
                        title = uiState.title,
                        hintResId = R.string.search_hint,
                        onClick = onShowSearch
                    )
                }
            )
        },
        onSuccess = {
            ShortDefContent(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollConnection),
                topContentPadding = TOP_APPBAR_HEIGHT.dp,
                bottomContentPadding = 80.dp,
                scrollState = scrollState,
                shortDefs = uiState.shortDefs,
                addToFavorites = onAddToFavorites,
                removeFromFavorites = onRemoveFromFavorites,
                onClick = onSelectShortDef,
                bringIntoViewShortDefId = uiState.defId
            )
        },
        onLoading = {
            ProgressBar()
        },
        onError = {
            LoadingError(retry = onRetry)
        }
    )
}

@Composable
private fun RandomWordFloatingButton(modifier: Modifier, onClick: () -> Unit) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_shuffle),
            contentDescription = null
        )
    }
}


@Composable
@Preview
private fun SearchBarPreview() {
    SearchBar(title = "Title",
        hintResId = R.string.search_hint,
        onClick = {}
    )
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    title: String,
    hintResId: Int,
    onClick: () -> Unit
) {
    SearchButton(
        modifier = modifier,
        title = title,
        hintResId = hintResId,
        onClick = onClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun SearchButton(
    modifier: Modifier = Modifier,
    title: String = "",
    @StringRes hintResId: Int = R.string.search_hint,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .height(TOP_APPBAR_HEIGHT.dp)
            .padding(8.dp)
            .fillMaxWidth(),
        onClick = onClick,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                imageVector = Icons.Filled.Search,
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(end = 32.dp),
                text = title.ifBlank { stringResource(id = hintResId) },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
