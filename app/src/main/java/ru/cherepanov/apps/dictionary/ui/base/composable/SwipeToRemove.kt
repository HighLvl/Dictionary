package ru.cherepanov.apps.dictionary.ui.base.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToRemove(onRemove: () -> Unit, content: @Composable () -> Unit) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToStart) {
                onRemove()
            }
            true
        }
    )
    SwipeToDismiss(
        directions = setOf(DismissDirection.EndToStart),
        state = dismissState,
        dismissThresholds = { FractionalThreshold(0.2f) },
        background = {
            val isVisible = dismissState.offset.value < with(LocalDensity.current) { -40.dp.toPx() }
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(initialAlpha = 0.3f),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                }
            }
        }
    ) {
        content()
    }
}
