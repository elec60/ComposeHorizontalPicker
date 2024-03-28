package mousavi.hashem.composehorizontalpicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen() {
    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(
                24.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var range by remember {
                mutableFloatStateOf(0f)
            }
            Column(
                modifier = Modifier.layout { measurable, constraints ->
                    val minWidth = constraints.minWidth
                    val maxWidth = constraints.maxWidth
                    val placable = measurable.measure(
                        constraints.copy(
                            minWidth = minWidth + 48.dp.roundToPx(),
                            maxWidth = maxWidth + 48.dp.roundToPx()
                        )
                    )
                    layout(placable.width, placable.height) {
                        placable.place(0, 0)
                    }
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var selectedItem by remember {
                    mutableFloatStateOf(0f)
                }
                Text(text = "${Math.round(selectedItem * 10) / 10.0}", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalPicker(
                    config = Config(count = 10, spacing = 30 * range + 30),
                    onValueChange = { selectedItem = it }
                )
            }

            Column {
                Text(text = "Spacing")
                Slider(
                    value = range,
                    onValueChange = { range = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalPicker(config: Config, onValueChange: (Float) -> Unit) {
    val totalSteps = remember(config) {
        config.steps * config.count
    }
    var width by remember {
        mutableIntStateOf(0)
    }

    val state = rememberLazyListState()

    LaunchedEffect(state) {
        snapshotFlow { state.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect {
                onValueChange((1f / config.steps) * it)
            }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            state = state,
            modifier = Modifier
                .onSizeChanged {
                    width = it.width
                }
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(config.spacing.toDp()),
            verticalAlignment = Alignment.Bottom,
            contentPadding = PaddingValues(horizontal = (width / 2).toDp()),
            flingBehavior = rememberSnapFlingBehavior(state)
        ) {
            for (i in 0..totalSteps) {
                val reminder = i % config.steps
                item {
                    VerticalDivider(
                        height = if (reminder == 0) 20.dp else 10.dp,
                        color = if (reminder == 0) Color.DarkGray else Color.Gray,
                        showText = reminder == 0,
                        index = i / config.steps
                    )
                }
            }
        }

        VerticalDivider(
            height = 40.dp,
            width = 2.dp,
            color = Color.Black.copy(alpha = .8f)
        )
    }
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    width: Dp = 1.dp,
    height: Dp = 10.dp,
    color: Color = Color.DarkGray,
    showText: Boolean = false,
    index: Int = 0
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(
            color = color,
            modifier = Modifier
                .height(height)
                .width(width)
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (showText) {
            val style = TextStyle(
                fontSize = 12.sp,
                color = Color.Black
            )
            val textMeasurer = rememberTextMeasurer()
            val textLayoutResult = remember("$index", style) {
                textMeasurer.measure("$index", style)
            }
            Canvas(modifier = Modifier) {
                drawText(
                    textLayoutResult, topLeft = Offset(
                        x = center.x - textLayoutResult.size.width / 2,
                        y = 5.dp.toPx()
                    )
                )
            }
        }
    }
}

@Composable
private fun Int.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }

@Composable
private fun Float.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
