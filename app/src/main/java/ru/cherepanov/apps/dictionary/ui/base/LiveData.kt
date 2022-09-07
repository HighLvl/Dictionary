package ru.cherepanov.apps.dictionary.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData

@Composable
@Suppress("UNCHECKED_CAST")
fun <T> LiveData<T>.observeUiState(): State<T> = observeAsState(value) as State<T>