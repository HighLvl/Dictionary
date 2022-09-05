package ru.cherepanov.apps.dictionary.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getSelectedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.cherepanov.apps.dictionary.ui.FormattedWordDef
import ru.cherepanov.apps.dictionary.ui.base.composable.*
import ru.cherepanov.apps.dictionary.ui.base.composable.preview.formattedWordDefStub


@Composable
fun DefDetailsScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {}
) {
    DefDetailsScreen(
        modifier = modifier,
        viewModel = hiltViewModel(),
        onBackPressed = onBackPressed
    )
}

@Composable
private fun DefDetailsScreen(
    modifier: Modifier,
    viewModel: DetailsViewModel,
    onBackPressed: () -> Unit
) {
    val fullDefResourceNullable by viewModel.uiState.observeAsState()
    val resource = fullDefResourceNullable ?: return

    ResourceScaffold(
        modifier = modifier,
        resource = resource,
        topBar = {
            val fullDef = resource.data
            DetailsTopAppBar(
                num = fullDef.num,
                title = fullDef.id.title,
                onBackPressed = onBackPressed
            )
        },
        floatingActionButton = {
            if (resource.isSuccess()) {
                FavoriteButton(
                    modifier = Modifier.padding(bottom = 8.dp, end = 16.dp),
                    isFavorite = resource.data.isFavorite,
                    onCheckedChange = {
                        if (it) {
                            viewModel.onAddToFavorites()
                        } else {
                            viewModel.onRemoveFromFavorites()
                        }
                    }
                )
            }
        },
        onSuccess = { contentPadding, fullDef ->
            Column(modifier = Modifier.padding(contentPadding)) {
                DefDetailsMainContent(
                    modifier = Modifier
                        .fillMaxWidth(),
                    fullDef = derivedStateOf { fullDef }.value
                )
                if (!fullDef.isFull) {
                    LoadingError(
                        modifier = Modifier.fillMaxWidth(),
                        retry = viewModel::retry
                    )
                }
            }
        },
        onLoading = { contentPadding, _ ->
            ProgressBar(modifier = Modifier.padding(contentPadding))
        },
        onError = {
            LoadingError(modifier = Modifier.padding(it), retry = viewModel::retry)
        }
    )
}

@Composable
private fun DefDetailsMainContent(
    modifier: Modifier,
    fullDef: FormattedWordDef,
) {
    DefDetailsPanel(
        modifier = modifier,
        fullDef = fullDef
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsTopAppBar(
    num: String?,
    title: String,
    onBackPressed: () -> Unit
) {
    Column {
        SmallTopAppBar(
            title = {
                DefTitle(num = num, title = title)
            },
            navigationIcon = {
                BackButton(onBackPressed = onBackPressed)
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        )
        Divider(thickness = 1.dp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun DefDetailsPanel(
    modifier: Modifier = Modifier,
    fullDef: FormattedWordDef = formattedWordDefStub()
) {
    val text = fullDef.getDetailsAnnotatedString()
    var textFieldValue by remember { mutableStateOf(TextFieldValue(text)) }
    CompositionLocalProvider(
        LocalTextToolbar provides AppTextToolbar(
            LocalView.current,
            provideSelectedText = { textFieldValue.getSelectedText().text }
        )
    ) {
        TextField(
            modifier = modifier,
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
            readOnly = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Unspecified,
                unfocusedIndicatorColor = Color.Unspecified,
                focusedIndicatorColor = Color.Unspecified
            )
        )
    }
}

@Composable
private fun FavoriteButton(
    modifier: Modifier,
    isFavorite: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = {
            onCheckedChange(!isFavorite)
        }
    ) {
        FavoriteIcon(isFavorite = isFavorite)
    }
}

