package com.sectra.noiseshield.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sectra.noiseshield.R

@Composable
fun Warning(
    showWarning: Boolean,
    modifier: Modifier,
    accentColor: Color,
) {
    Box(modifier = modifier) {
        if (showWarning) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Icon(
                    Icons.Default.Warning,
                    tint = accentColor,
                    contentDescription = stringResource(R.string.warning_icon_description),
                    modifier = Modifier.size(56.dp),
                )
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(start = 16.dp),
                ) {
                    Text(
                        stringResource(
                            R.string.volume_to_low_warning_title
                        ),
                        color = accentColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                    )
                    Text(
                        stringResource(
                            R.string.volume_to_low_warning_text
                        ),
                        color = accentColor,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Visible
                    )
                }
            }
        }
    }
}

@Preview(backgroundColor = 0xFF0000, showBackground = true)
@Composable
private fun PreviewWarning() {
    MaterialTheme {
        Warning(
            showWarning = true, Modifier, Color.White
        )
    }
}