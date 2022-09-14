package ru.cherepanov.apps.dictionary.ui.base.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DefList(
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues,
    content: LazyListScope.() -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = scrollState,
        contentPadding = contentPadding,
        content = content
    )
}