package com.sectra.noiseshield

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sectra.noiseshield.ui.NoiseScreen
import com.sectra.noiseshield.ui.theme.primaryYellow
import com.sectra.noiseshield.ui.theme.primaryBlue

class MainActivity : ComponentActivity() {

    private lateinit var noiseService: NoiseService
    private lateinit var noiseSettings: NoiseSettings
    private var isBound = mutableStateOf(false)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            noiseService = (binder as NoiseService.NoiseServiceBinder).getService()
            isBound.value = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startTime = System.currentTimeMillis()
        installSplashScreen().setKeepOnScreenCondition {
            val elapsedTime = System.currentTimeMillis() - startTime
            elapsedTime < 2000
        }

        noiseSettings = NoiseSettings(this)

        Intent(this, NoiseService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                if (isBound.value) {
                    NoiseScreen(noiseService, noiseSettings)
                } else {
                    val hasSetting = noiseSettings.appVolumeRestrictionExist()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(if (hasSetting) primaryBlue else primaryYellow)
                    ) {}
                }
            }
        }
    }
}
