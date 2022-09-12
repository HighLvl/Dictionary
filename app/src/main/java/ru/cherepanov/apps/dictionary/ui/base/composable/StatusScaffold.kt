package ru.cherepanov.apps.dictionary.ui.base.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.cherepanov.apps.dictionary.ui.base.viewModel.Status

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusScaffold(
    modifier: Modifier = Modifier,
    status: Status,
    topBar: @Composable () -> Unit = {},
    toolBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    onSuccess: @Composable () -> Unit = { },
    onLoading: @Composable () -> Unit = { },
    onError: @Composable () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        floatingActionButton = floatingActionButton
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            when (status) {
                Status.SUCCESS -> onSuccess()
                Status.LOADING -> onLoading()
                Status.ERROR -> onError()
            }
            Box(modifier = Modifier.align(Alignment.TopCenter)) {
                toolBar()
            }
        }
    }
}