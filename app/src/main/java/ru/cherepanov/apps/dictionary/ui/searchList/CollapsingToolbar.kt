package ru.cherepanov.apps.dictionary.ui.searchList

import android.os.Parcelable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize

@Composable
@Preview
private fun Preview() {
    val scrollState = rememberLazyListState()
    val toolbarState = rememberToolbarState(height = with(LocalDensity.current) { 60.dp.toPx() })
    val scrollConnection = rememberCollapsingToolbarConnection(
        toolbarState = toolbarState,
        scrollableState = scrollState
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollConnection)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = scrollState
        ) {
            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
            repeat(100) {
                item {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .padding(4.dp)
                            .height(60.dp)
                            .background(Color.Red)
                    )
                }
            }
        }

        CollapsingToolbar(
            modifier = Modifier.align(Alignment.TopCenter),
            toolbarState = toolbarState,
            toolBar = {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(60.dp)
                        .background(Color.Blue)
                )
            }
        )
    }
}

@Composable
fun CollapsingToolbar(
    modifier: Modifier = Modifier,
    toolbarState: ToolbarState,
    toolBar: @Composable () -> Unit
) {
    Box(modifier = modifier
        .offset { IntOffset(0, toolbarState.offset.toInt()) }) {
        toolBar()
    }
}

@Composable
fun rememberToolbarState(
    height: Float,
    initialState: ToolbarState.State = ToolbarState.State.EXPANDED
) = rememberSaveable { ToolbarState(height, initialState) }


@Parcelize
class ToolbarState(
    private val height: Float,
    private val initialState: State
) : Parcelable {
    val offsetLimit: Float = -height

    var offset by mutableStateOf(
        when (initialState) {
            State.COLLAPSED -> offsetLimit
            State.EXPANDED -> 0f
        }
    )
        private set

    val collapsedFraction: Float
        get() = if (offsetLimit != 0f) {
            offset / offsetLimit
        } else {
            0f
        }

    fun scrollBy(delta: Float) {
        val newOffset = offset + delta
        offset = newOffset.coerceIn(offsetLimit, 0f)
    }

    fun expand() {
        offset = 0f
    }

    fun collapse() {
        offset = offsetLimit
    }

    enum class State {
        COLLAPSED, EXPANDED
    }
}

@Composable
fun rememberCollapsingToolbarConnection(
    toolbarState: ToolbarState,
    scrollableState: ScrollableState
) = remember {
    CollapsingToolbarConnection(toolbarState, scrollableState)
}

class CollapsingToolbarConnection(
    private val toolbarState: ToolbarState,
    private val scrollableState: ScrollableState
) : NestedScrollConnection {
    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        if (consumed.getDistance() == 0f) return super.onPostScroll(consumed, available, source)
        toolbarState.scrollBy(consumed.y)
        return super.onPostScroll(consumed, available, source)
    }

    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity
    ): Velocity {
        if (toolbarState.collapsedFraction == 0f ||
            toolbarState.collapsedFraction == 1f
        ) {
            return super.onPostFling(consumed, available)
        }

        var previousValue = 0f
        scrollableState.scroll {
            val targetOffset = if (toolbarState.collapsedFraction > SCROLL_UP_COLLAPSED_FRACTION)
                toolbarState.offsetLimit else 0f
            animate(
                initialValue = 0f,
                targetValue = toolbarState.offset - targetOffset,
                animationSpec = tween(
                    durationMillis = ANIMATION_DURATION_MILLIS,
                    easing = FastOutSlowInEasing
                )
            ) { currentValue, _ ->
                val delta = currentValue - previousValue
                previousValue += scrollBy(delta)
                toolbarState.scrollBy(-delta)
            }
        }
        return super.onPostFling(consumed, available)
    }

    private companion object {
        const val SCROLL_UP_COLLAPSED_FRACTION = 0.5f
        const val ANIMATION_DURATION_MILLIS = 200
    }
}