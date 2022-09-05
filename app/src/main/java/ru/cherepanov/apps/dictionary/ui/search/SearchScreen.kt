package ru.cherepanov.apps.dictionary.ui.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import ru.cherepanov.apps.dictionary.ui.base.composable.BackButton
import ru.cherepanov.apps.dictionary.ui.base.composable.LoadingError
import ru.cherepanov.apps.dictionary.ui.base.composable.ResourceScaffold

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
    val resourceNullable by viewModel.uiState.observeAsState()
    val resource = resourceNullable!!

    BackHandler {
        onBackPressed()
    }

    ResourceScaffold(
        modifier = modifier,
        resource = resource,
        topBar = {
            SearchBar(
                searchTerm = resource.data.searchTerm,
                isLoading = resource.isLoading(),
                onValueChange = viewModel::onFetchSuggestions,
                onBackPressed = onBackPressed
            )
        },
        onSuccess = { contentPadding, searchState ->
            LazyColumn(modifier = Modifier.padding(contentPadding)) {
                items(searchState.suggestions) { suggestion ->
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

@Composable
@Preview
private fun SearchBarPreview() {
    SearchBar(
        searchTerm = "",
        isLoading = true,
        onValueChange = {},
        onBackPressed = {})
}

@Composable
private fun SearchBar(
    searchTerm: String,
    isLoading: Boolean,
    onValueChange: (String) -> Unit,
    onBackPressed: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onBackPressed)
            SearchTextField(searchTerm, isLoading, onValueChange)
        }
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
