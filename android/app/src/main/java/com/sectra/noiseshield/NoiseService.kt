package com.sectra.noiseshield

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.sectra.noiseshield.player.NoisePlayer
import com.sectra.noiseshield.volumecontroller.VolumeController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class NoiseService : Service() {

    companion object {
        private const val CHANNEL_ID = "NoiseForeground"
        private const val NOTIFICATION_ID = 101
    }

    inner class NoiseServiceBinder : Binder() {
        fun getService(): NoiseService = this@NoiseService
    }

    lateinit var noisePlayer: NoisePlayer
    lateinit var volumeController: VolumeController
    private var runningAsForeground = false

    private val binder = NoiseServiceBinder()
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        volumeController = VolumeController(
            getSystemService(AUDIO_SERVICE) as AudioManager,
            NoiseSettings(this),
        )

        noisePlayer = NoisePlayer(CoroutineScope(Job() + Dispatchers.Main), volumeController)

        contentResolver.registerContentObserver(
            android.provider.Settings.System.CONTENT_URI, true, volumeController
        )

        super.onCreate()
    }

    fun toggle() {
        if (runningAsForeground) {
            stopForegroundService()
        } else {
            startForegroundService()
        }
        noisePlayer.toggle()
    }

    private fun stopForegroundService() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        runningAsForeground = false
    }

    private fun startForegroundService() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.service_notification_name, getString(R.string.app_name)),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(getString(R.string.service_notification_title))
            .setContentText(getString(R.string.service_notification_text)).build()
        startForeground(NOTIFICATION_ID, notification)
        runningAsForeground = true
    }

    override fun onDestroy() {
        stopForegroundService()
        contentResolver.unregisterContentObserver(volumeController)
        noisePlayer.restoreVolume()
        noisePlayer.destroy()
        super.onDestroy()
    }
}