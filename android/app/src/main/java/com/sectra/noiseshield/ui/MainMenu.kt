package com.sectra.noiseshield.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sectra.noiseshield.NoiseSettings
import com.sectra.noiseshield.R

@Composable
fun MainMenu(noiseSettings: NoiseSettings, onDismiss: () -> Unit) {
    var sliderValue by remember { mutableFloatStateOf(noiseSettings.getMinimumVolumePercent().toFloat()) }

    var minimumVolumeText = stringResource(R.string.minimum_volume_unmanaged, sliderValue.toInt())
    if (noiseSettings.mdmVolumeRestrictionExist()) {
        minimumVolumeText = stringResource(R.string.minimum_volume_managed, sliderValue.toInt())
    }

    AlertDialog(onDismissRequest = { onDismiss() }, title = {
        Text(
            stringResource(R.string.app_name)
        )
    }, text = {
        Column {
            Text(minimumVolumeText)
            Slider(
                value = sliderValue,
                onValueChange = {
                    sliderValue = it
                    noiseSettings.setMinimumVolumePercentUser(it)
                },
                valueRange = 0f..100f,
                steps = 100,
                enabled = !noiseSettings.mdmVolumeRestrictionExist()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(stringResource(R.string.version, stringResource(R.string.versionName)))
        }
    }, confirmButton = {
        Button(onClick = { onDismiss() }) {
            Text(stringResource(R.string.close))
        }
    })
}
