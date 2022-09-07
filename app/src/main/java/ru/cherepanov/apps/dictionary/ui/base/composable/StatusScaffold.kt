package ru.cherepanov.apps.dictionary.ui.base.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.cherepanov.apps.dictionary.ui.base.viewModel.Status

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusScaffold(
    modifier: Modifier = Modifier,
    status: Status,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    onSuccess: @Composable (PaddingValues) -> Unit = { },
    onLoading: @Composable (PaddingValues) -> Unit = { },
    onError: @Composable (PaddingValues) -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        floatingActionButton = floatingActionButton
    ) {
        when (status) {
            Status.SUCCESS -> onSuccess(it)
            Status.LOADING -> onLoading(it)
            Status.ERROR -> onError(it)
        }
    }
}