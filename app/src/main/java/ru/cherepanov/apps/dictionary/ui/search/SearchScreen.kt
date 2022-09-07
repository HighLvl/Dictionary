package ru.cherepanov.apps.dictionary.ui.search

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import ru.cherepanov.apps.dictionary.R
import ru.cherepanov.apps.dictionary.domain.model.Filter
import ru.cherepanov.apps.dictionary.ui.base.composable.BackButton
import ru.cherepanov.apps.dictionary.ui.base.composable.LoadingError
import ru.cherepanov.apps.dictionary.ui.base.composable.StatusScaffold
import ru.cherepanov.apps.dictionary.ui.base.observeUiState
import ru.cherepanov.apps.dictionary.ui.base.viewModel.Status

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onSelectSuggestion: (String) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    SearchScreen(
        modifier = modifier,
        viewModel = hiltViewModel(),
        onBackPressed = onBackPressed,
        onSelectSuggestion = onSelectSuggestion
    )
}


@Composable
private fun SearchScreen(
    modifier: Modifier,
    viewModel: SearchViewModel,
    onBackPressed: () -> Unit,
    onSelectSuggestion: (String) -> Unit
) {
    val uiState by viewModel.uiState.observeUiState()

    BackHandler {
        onBackPressed()
    }

    StatusScaffold(
        modifier = modifier,
        status = uiState.status,
        topBar = {
            SearchBar(
                searchTerm = uiState.searchTerm,
                onValueChange = viewModel::onChangeSearchTerm,
                isLoading = uiState.status == Status.LOADING,
                filterState = uiState.filter.mapToFilterState(),
                onChangeFilterState = { viewModel.onChangeFilter(it.mapToFilter()) },
                onBackPressed = onBackPressed
            )
        },
        onSuccess = { contentPadding ->
            LazyColumn(modifier = Modifier.padding(contentPadding)) {
                items(uiState.suggestions) { suggestion ->
                    SuggestionItem(suggestion, onClick = { onSelectSuggestion(suggestion) })
                }
            }
        },
        onError = {
            LoadingError(
                modifier = Modifier.padding(it),
                retry = viewModel::retry
            )
        }
    )
}

private fun FilterState.mapToFilter(): Filter =
    Filter(
        searchMode = when (searchMode) {
            FilterState.SearchMode.FUZZY -> Filter.SearchMode.FUZZY
            FilterState.SearchMode.PREFIX -> Filter.SearchMode.PREFIX
        }
    )

private fun Filter.mapToFilterState(): FilterState =
    FilterState(
        searchMode = when (searchMode) {
            Filter.SearchMode.FUZZY -> FilterState.SearchMode.FUZZY
            Filter.SearchMode.PREFIX -> FilterState.SearchMode.PREFIX
        }
    )

@Composable
private fun SuggestionItem(suggestion: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(start = 32.dp, end = 24.dp),
            imageVector = Icons.Filled.Search,
            contentDescription = null
        )
        Text(
            text = suggestion
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    searchTerm: String,
    filterState: FilterState,
    onChangeFilterState: (FilterState) -> Unit,
    isLoading: Boolean,
    onValueChange: (String) -> Unit,
    onBackPressed: () -> Unit,
) {
    Column {
        SmallTopAppBar(
            navigationIcon = {
                BackButton(
                    onBackPressed = onBackPressed
                )
            },
            actions = {
                SearchTextField(
                    initialText = searchTerm,
                    isLoading = isLoading,
                    onValueChange = onValueChange
                )
                Filter(
                    state = filterState,
                    onChangeState = onChangeFilterState
                )
            },
            title = {})
        Divider(thickness = 1.dp)
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun SearchTextField(
    initialText: String,
    isLoading: Boolean,
    onValueChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var textFieldValue by remember(initialText != "") {
        mutableStateOf(TextFieldValue(initialText).run {
            copy(selection = TextRange(initialText.length))
        })
    }
    var keyboardShowed by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (keyboardShowed) return@LaunchedEffect
        focusRequester.requestFocus()
        delay(200)
        keyboardController?.show()
        keyboardShowed = true
    }

    Box {
        TextField(
            modifier = Modifier.focusRequester(focusRequester),
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                onValueChange(it.text)
            },
            singleLine = true,
            placeholder = {
                Text(text = stringResource(id = R.string.search_hint))
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Unspecified,
                unfocusedIndicatorColor = Color.Unspecified,
                focusedIndicatorColor = Color.Unspecified
            )
        )
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(24.dp),
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
@Preview
private fun FilterPreview() {
    var state by remember {
        mutableStateOf(FilterState())
    }

    Filter(
        state = state,
        onChangeState = { state = it }
    )
}

@Composable
private fun Filter(state: FilterState, onChangeState: (FilterState) -> Unit) {
    var showFilterMenu by rememberSaveable { mutableStateOf(false) }

    Box {
        IconButton(onClick = { showFilterMenu = !showFilterMenu }) {
            Icon(imageVector = Icons.Filled.FilterList, contentDescription = null)
        }
        FilterMenu(
            expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false },
            state = state, onChangeState = onChangeState
        )
    }
}

@Composable
private fun FilterMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    state: FilterState,
    onChangeState: (FilterState) -> Unit
) {
    DropdownMenu(
        modifier = Modifier.padding(16.dp),
        expanded = expanded, onDismissRequest = onDismissRequest
    ) {
        Column {
            Text(text = stringResource(id = R.string.search_mode_label))
            for (searchMode in FilterState.SearchMode.values()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = state.searchMode == searchMode,
                        onClick = { onChangeState(state.copy(searchMode = searchMode)) }
                    )
                    Text(text = stringResource(id = searchMode.titleResId))
                }
            }
        }
    }
}

private data class FilterState(val searchMode: SearchMode = SearchMode.PREFIX) {
    enum class SearchMode(@StringRes val titleResId: Int) {
        PREFIX(R.string.search_mode_prefix), FUZZY(R.string.search_mode_fuzzy)
    }
}