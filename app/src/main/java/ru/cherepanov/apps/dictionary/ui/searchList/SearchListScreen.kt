package ru.cherepanov.apps.dictionary.ui.searchList

import androidx.annotation.StringRes
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.cherepanov.apps.dictionary.R
import ru.cherepanov.apps.dictionary.domain.model.DefId
import ru.cherepanov.apps.dictionary.ui.base.viewModel.Resource
import ru.cherepanov.apps.dictionary.ui.base.composable.*


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
    val nullableResource by viewModel.uiState.observeAsState()
    val resource = nullableResource!!
    SearchListScreen(
        modifier = modifier,
        resource = resource,
        onBackPressed = onBackPressed,
        onSelectShortDef = onSelectShortDef,
        onLoadRandomWord = viewModel::onLoadRandomWord,
        onShowSearch = { onShowSearch(resource.data.title ?: "") },
        onAddToFavorites = viewModel::onAddToFavorites,
        onRemoveFromFavorites = viewModel::onRemoveFromFavorites,
        onIntoViewBrought = viewModel::onIntoViewBrought,
        onRetry = viewModel::retry
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchListScreen(
    modifier: Modifier,
    resource: Resource<SearchListUiState>,
    onBackPressed: (() -> Unit)?,
    onSelectShortDef: (DefId) -> Unit,
    onLoadRandomWord: () -> Unit,
    onShowSearch: () -> Unit,
    onAddToFavorites: (DefId) -> Unit,
    onRemoveFromFavorites: (DefId) -> Unit,
    onIntoViewBrought: () -> Unit,
    onRetry: () -> Unit
) {
    var offset by remember {
        mutableStateOf(0f)
    }
    val scrollBehaviour = rememberScrollBehaviour(onContentOffsetChange = { offset = it })
    ResourceScaffold(
        modifier = modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        resource = resource,
        topBar = {
            Column {
                TitleTopBar(titleResId = R.string.search_label,
                    navigationIcon = {
                        onBackPressed?.let { BackButton(onBackPressed = it) }
                    }
                )
                SearchBar(
                    title = resource.data.wordTitle,
                    hintResId = R.string.search_hint,
                    onClick = onShowSearch,
                    scrollBehaviour = scrollBehaviour,
                    contentOffset = with(LocalDensity.current) { offset.toDp() }
                )
            }
        },
        floatingActionButton = {
            RandomWordFloatingButton(
                modifier = Modifier.padding(bottom = 8.dp, end = 16.dp),
                onClick = onLoadRandomWord
            )
        },
        onSuccess = { contentPadding, uiState ->
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
            ) {
                ShortDefContent(
                    modifier = Modifier.fillMaxSize(),
                    bottomContentPadding = 80.dp,
                    shortDefs = uiState.shortDefs,
                    addToFavorites = onAddToFavorites,
                    removeFromFavorites = onRemoveFromFavorites,
                    onClick = onSelectShortDef,
                    bringIntoViewShortDefId = uiState.defId,
                    onIntoViewBrought = onIntoViewBrought
                )
            }
        },
        onLoading = { contentPAdding, _ ->
            ProgressBar(modifier = Modifier.padding(contentPAdding))
        },
        onError = {
            LoadingError(
                modifier = Modifier.padding(it), retry = onRetry
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    title: String,
    hintResId: Int,
    onClick: () -> Unit,
    scrollBehaviour: TopAppBarScrollBehavior? = null,
    contentOffset: Dp
) {
    SmallTopAppBar(
        modifier = modifier,
        title = {},
        actions = {
            SearchButton(
                modifier = Modifier
                    .offset(y = contentOffset)
                    .padding(start = 4.dp)
                    .padding(4.dp),
                title = title,
                hintResId = hintResId,
                onClick = onClick
            )
        },
        scrollBehavior = scrollBehaviour
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
        modifier = modifier.height(IntrinsicSize.Min),
        onClick = onClick,
        shadowElevation = 4.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp, end = 8.dp),
                imageVector = Icons.Filled.Search,
                contentDescription = null
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 32.dp)
            ) {
                Text(

                    text = title.ifBlank { stringResource(id = hintResId) },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )
                Divider(thickness = 1.dp)
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun rememberScrollBehaviour(onContentOffsetChange: (Float) -> Unit): TopAppBarScrollBehavior {
    val appBarState = rememberTopAppBarState()

    val scrollBehaviour = remember {
        object : TopAppBarScrollBehavior {
            override val isPinned: Boolean = false
            override val nestedScrollConnection =
                SearchBarScrollConnection(appBarState, onContentOffsetChange)
            override val state: TopAppBarState = appBarState
        }
    }
    return scrollBehaviour
}

@OptIn(ExperimentalMaterial3Api::class)
private class SearchBarScrollConnection(
    private val state: TopAppBarState,
    private val onContentOffsetChange: (Float) -> Unit
) : NestedScrollConnection {
    private var isAnimationRunning = false

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        if (isAnimationRunning) return Offset.Zero
        state.heightOffset += available.y
        updateContentOffset()
        return if (state.heightOffset == 0f || state.heightOffset == state.heightOffsetLimit) {
            Offset.Zero
        } else {
            available.copy(x = 0f)
        }
    }

    private fun updateContentOffset() {
        val newOffset = (state.heightOffset / 2).coerceIn(state.heightOffsetLimit, 0f)
        onContentOffsetChange(newOffset)
    }

    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity
    ): Velocity {
        if (isAnimationRunning ||
            state.heightOffset == state.heightOffsetLimit ||
            state.heightOffset == 0f
        ) {
            return Velocity.Zero
        }
        isAnimationRunning = true
        AnimationState(initialValue = state.heightOffset).animateTo(
            targetValue = if (state.collapsedFraction > scrollUpCollapsedFraction)
                state.heightOffsetLimit else 0f,
            animationSpec = tween(
                durationMillis = animationDurationMillis,
                easing = LinearOutSlowInEasing
            )
        ) {
            state.heightOffset = value
            updateContentOffset()
        }
        isAnimationRunning = false
        return Velocity.Zero
    }

    private companion object {
        private const val scrollUpCollapsedFraction = 0.5f
        private const val animationDurationMillis = 150
    }
}