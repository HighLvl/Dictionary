package ru.cherepanov.apps.dictionary.ui.base.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.cherepanov.apps.dictionary.ui.FormattedWordDef

@Composable
fun DefList(
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues,
    shortDefs: List<FormattedWordDef>,
    item: @Composable LazyItemScope.(FormattedWordDef) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = scrollState,
        contentPadding = contentPadding
    ) {
        items(shortDefs, key = { it.id }) {
            item(it)
        }
    }
}