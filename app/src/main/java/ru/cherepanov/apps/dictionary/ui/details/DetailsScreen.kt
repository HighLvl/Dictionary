package ru.cherepanov.apps.dictionary.ui.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getSelectedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.cherepanov.apps.dictionary.ui.AnnotationTag
import ru.cherepanov.apps.dictionary.ui.FormattedWordDef
import ru.cherepanov.apps.dictionary.ui.base.composable.*
import ru.cherepanov.apps.dictionary.ui.base.composable.preview.formattedWordDefStub
import ru.cherepanov.apps.dictionary.ui.base.composable.theme.wordLinkColor
import ru.cherepanov.apps.dictionary.ui.base.observeUiState
import ru.cherepanov.apps.dictionary.ui.base.viewModel.Status
import ru.cherepanov.apps.dictionary.ui.searchList.detectTapUnconsumed
import ru.cherepanov.apps.dictionary.ui.toFormatted


@Composable
fun DefDetailsScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
    onClickWord: (String) -> Unit
) {
    DefDetailsScreen(
        modifier = modifier,
        viewModel = hiltViewModel(),
        onBackPressed = onBackPressed,
        onClickWord
    )
}

@Composable
private fun DefDetailsScreen(
    modifier: Modifier,
    viewModel: DetailsViewModel,
    onBackPressed: () -> Unit,
    onClickWord: (String) -> Unit
) {
    val uiState by viewModel.uiState.observeUiState()

    val wordLinkColor = MaterialTheme.colorScheme.wordLinkColor
    val fullDef = remember(uiState.wordDef) {
        uiState.wordDef.toFormatted(
            isDetails = true,
            wordLinkColor = wordLinkColor
        )
    }

    StatusScaffold(
        modifier = modifier,
        status = uiState.status,
        topBar = {
            DetailsTopAppBar(
                num = fullDef.num,
                title = fullDef.id.title,
                onBackPressed = onBackPressed
            )
        },
        floatingActionButton = {
            if (uiState.status == Status.SUCCESS) {
                FavoriteButton(
                    modifier = Modifier.padding(bottom = 8.dp, end = 16.dp),
                    isFavorite = uiState.wordDef.isFavorite,
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
        onSuccess = {
            Column {
                DefDetailsMainContent(
                    modifier = Modifier.fillMaxWidth(),
                    fullDef = fullDef,
                    onClickWord = onClickWord
                )
                if (!uiState.wordDef.isFull) {
                    LoadingError(
                        modifier = Modifier.fillMaxWidth(),
                        retry = viewModel::retry
                    )
                }
            }
        },
        onLoading = {
            ProgressBar()
        },
        onError = {
            LoadingError(retry = viewModel::retry)
        }
    )
}

@Composable
private fun DefDetailsMainContent(
    modifier: Modifier,
    fullDef: FormattedWordDef,
    onClickWord: (String) -> Unit,
) {
    DefDetailsPanel(
        modifier = modifier.padding(horizontal = 8.dp),
        fullDef = fullDef,
        onClickWord = onClickWord
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
private fun DefDetailsPanel(
    modifier: Modifier = Modifier,
    fullDef: FormattedWordDef = formattedWordDefStub(),
    onClickWord: (String) -> Unit = {}
) {

    val text = fullDef.getDetailsAnnotatedString()
    var textFieldValue by remember { mutableStateOf(TextFieldValue(text)) }

    val onClickText: (Int) -> Unit = { offset ->
        text.getStringAnnotations(AnnotationTag.URI.value, offset, offset)
            .firstOrNull()
            ?.let {
                onClickWord(it.item)
            }
    }

    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val scrollState = rememberScrollState()

    val pressIndicator = Modifier.pointerInput(Unit) {
        detectTapUnconsumed { pos ->
            layoutResult.value?.let { layoutResult ->
                val localPos = layoutResult.getOffsetForPosition(
                    pos + Offset(
                        0f,
                        scrollState.value.toFloat()
                    )
                )
                onClickText(localPos)
            }
        }
    }

    CompositionLocalProvider(
        LocalTextToolbar provides AppTextToolbar(
            LocalView.current,
            provideSelectedText = { textFieldValue.getSelectedText().text }
        )
    ) {
        BasicTextField(
            modifier = modifier
                .then(pressIndicator)
                .verticalScroll(scrollState),
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
            readOnly = true,
            onTextLayout = {
                layoutResult.value = it
            }
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

