package com.sectra.noiseshield.ui

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.sp
import com.sectra.noiseshield.NoiseService
import com.sectra.noiseshield.NoiseSettings
import com.sectra.noiseshield.R
import com.sectra.noiseshield.ui.animation.WaveAnimation
import com.sectra.noiseshield.ui.theme.primaryGreen
import com.sectra.noiseshield.ui.theme.primaryRed
import com.sectra.noiseshield.ui.theme.primaryYellow
import com.sectra.noiseshield.ui.theme.primaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoiseScreen(
    noiseService: NoiseService,
    noiseSettings: NoiseSettings,
) {
    val volumeController = noiseService.volumeController
    val noisePlayer = noiseService.noisePlayer

    val background = when {
        !noiseSettings.appVolumeRestrictionExist() -> primaryYellow
        !noisePlayer.isPlaying -> primaryBlue
        volumeController.volumeToLow -> primaryRed
        else -> primaryGreen
    }
    val accentColor = when {
        !noiseSettings.appVolumeRestrictionExist() -> Color.Black
        else -> Color.White
    }

    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(title = {}, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent, actionIconContentColor = accentColor
        ), actions = {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    Icons.Filled.MoreVert,
                    stringResource(R.string.menu_icon_description),
                )
            }
        })

        Spacer(modifier = Modifier.weight(5f))

        if (noiseSettings.appVolumeRestrictionExist()) {
            Warning(
                volumeController.volumeToLow && noisePlayer.isPlaying,
                Modifier.weight(20f),
                accentColor
            )
        } else {
            Info(
                Modifier.weight(20f), accentColor
            )
        }

        Spacer(modifier = Modifier.weight(5f))

        if (noisePlayer.isPlaying) {
            WaveAnimation(
                modifier = Modifier.weight(15f), accentColor
            )
        } else {
            Box(modifier = Modifier.weight(15f)) {
                Text(
                    text = stringResource(R.string.press_play_to_generate_noise),
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
                    color = accentColor
                )
            }
        }

        Spacer(modifier = Modifier.weight(30f))
        PlayPauseButton(
            accentColor,
            background,
            noisePlayer,
            noiseService::toggle,
            Modifier.weight(10f),
        )
        Spacer(modifier = Modifier.weight(15f))
    }

    NoConfigurationFileDialog(
        showDialog = remember { mutableStateOf(noiseSettings.showNoConfigurationMessage()) },
        title = stringResource(R.string.volume_setting_unavailable_dialog_title),
        message = stringResource(
            R.string.volume_setting_unavailable_dialog_message, Build.MODEL
        ),
        noiseSettings::setAppRestrictionNoFileInfoShown,
    )

    if (showMenu) {
        MainMenu(noiseSettings = noiseSettings, onDismiss = {
            showMenu = false
            volumeController.updateVolumeToLow()
        })
    }
}