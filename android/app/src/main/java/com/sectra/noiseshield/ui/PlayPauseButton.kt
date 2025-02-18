package com.sectra.noiseshield.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.sectra.noiseshield.R
import com.sectra.noiseshield.player.IPlayer

@Composable
fun PlayPauseButton(
    accentColor: Color,
    tint: Color,
    player: IPlayer,
    togglePlay: () -> Unit,
    modifier: Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val icon = if (player.isPlaying) R.drawable.pause else R.drawable.play
    val iconDescription =
        if (player.isPlaying) stringResource(id = R.string.pause_icon_description) else stringResource(
            id = R.string.play_icon_description
        )
    Surface(
        color = accentColor,
        shape = CircleShape,
        modifier = modifier
            .aspectRatio(1f)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                togglePlay()
            },
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = iconDescription,
            tint = tint,
        )
    }
}

@Preview(backgroundColor = 0xFFF95041, showBackground = true)
@Composable
private fun PreviewPlayPauseButton() {
    val player = object : IPlayer {
        override val isPlaying: Boolean
            get() = true

        override fun toggle() {
        }
    }

    MaterialTheme {
        PlayPauseButton(Color.White, Color(0xFFF95041), player = player, {}, modifier = Modifier)
    }
}