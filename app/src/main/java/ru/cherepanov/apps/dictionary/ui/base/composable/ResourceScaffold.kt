package ru.cherepanov.apps.dictionary.ui.base.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.cherepanov.apps.dictionary.ui.base.viewModel.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ResourceScaffold(
    modifier: Modifier = Modifier,
    resource: Resource<T>,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    onSuccess: @Composable (PaddingValues, T) -> Unit = { _, _ -> },
    onLoading: @Composable (PaddingValues, T) -> Unit = { _, _ -> },
    onError: @Composable (PaddingValues) -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        floatingActionButton = floatingActionButton
    ) {
        when {
            resource.isSuccess() -> onSuccess(it, resource.data)
            resource.isLoading() -> onLoading(it, resource.data)
            resource.isError() -> onError(it)
        }
    }
}