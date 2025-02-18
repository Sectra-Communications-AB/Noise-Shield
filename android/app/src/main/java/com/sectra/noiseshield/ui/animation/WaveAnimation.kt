package com.sectra.noiseshield.ui.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun WaveAnimation(modifier: Modifier, accentColor: Color) {
    Box(modifier = modifier) {
        val barWidth = 8.dp
        val configuration = LocalConfiguration.current

        val screenWidth = configuration.screenWidthDp.dp
        val screenPercent = 0.4

        val numberOfBars = (screenWidth.value * screenPercent / (barWidth.value * 2)).toInt()

        val barHeights =
            remember { mutableStateListOf<Float>().apply { repeat(numberOfBars) { add(0f) } } }
        val maxBarHeight = 100f

        // Use LaunchedEffect to update heights periodically
        LaunchedEffect(Unit) {
            while (true) {
                delay(250)
                for (i in barHeights.indices) {
                    barHeights[i] = Random.nextFloat() * maxBarHeight
                }
            }
        }

        // Display the bars
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(barWidth),
            modifier = Modifier
                .wrapContentWidth()
                .height(maxBarHeight.dp)
                .align(Alignment.Center)
        ) {
            for (height in barHeights) {
                AnimatedBar(height.dp, barWidth, accentColor)
            }
        }
    }
}

@Composable
fun AnimatedBar(barHeight: Dp, barWidth: Dp, color: Color) {
    val animatedHeight by animateDpAsState(
        targetValue = barHeight,
        animationSpec = tween(durationMillis = 250, easing = LinearEasing),
        label = ""
    )

    Surface(
        color = color,
        modifier = Modifier
            .width(barWidth)
            .height(animatedHeight)
            .background(color, RoundedCornerShape(4.dp)), shape = RoundedCornerShape(4.dp)
    ) {}
}

@Preview(showBackground = true, backgroundColor = 0xFFF95041)
@Composable
private fun WaveAnimationPreview() {
    WaveAnimation(modifier = Modifier.fillMaxWidth(), Color.White)
}
