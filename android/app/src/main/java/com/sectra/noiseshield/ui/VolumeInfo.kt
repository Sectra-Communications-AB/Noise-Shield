package com.sectra.noiseshield.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sectra.noiseshield.R

@Composable
fun Info(
    modifier: Modifier,
    accentColor: Color,
) {
    var showDialog by remember {
        mutableStateOf(false)
    }
    Box(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.Center)
                .padding(horizontal = 16.dp)
                .clickable { showDialog = true },
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(
                Icons.Default.Info,
                tint = accentColor,
                contentDescription = stringResource(R.string.info_icon_description),
                modifier = Modifier.size(56.dp),
            )
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(start = 16.dp),
            ) {
                Text(
                    stringResource(R.string.no_volume_information_title),
                    color = accentColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                )
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text(text = stringResource(id = R.string.ok))
                }
            },
            title = { Text(text = stringResource(R.string.no_volume_information_title)) },
            text = { Text(text = stringResource(R.string.no_volume_information_text)) },
        )
    }
}

