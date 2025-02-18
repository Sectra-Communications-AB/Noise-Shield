package com.sectra.noiseshield.volumecontroller

import android.database.ContentObserver
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sectra.noiseshield.NoiseSettings

class VolumeController(
    private var audioManager: AudioManager,
    private var noiseSettings: NoiseSettings,
) : IVolumeController, ContentObserver(Handler(Looper.getMainLooper())) {

    private var minimumVolumePercent = noiseSettings.getMinimumVolumePercent()

    override var volumePercent by mutableFloatStateOf(getVolume())
    override var volumeToLow by mutableStateOf(getVolume() * 100 < minimumVolumePercent)

    override fun onChange(selfChange: Boolean) {
        val newVolume = getVolume()
        if (newVolume != volumePercent) {
            volumePercent = newVolume
            updateVolumeToLow()
        }

        super.onChange(selfChange)
    }

    override fun updateVolumeToLow () {
        minimumVolumePercent = noiseSettings.getMinimumVolumePercent()
        volumeToLow = volumePercent * 100 < minimumVolumePercent
    }

    private fun getVolume(): Float {
        val volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        val volumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        return volume / volumeMax
    }

    override fun getVolumeIndex() = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

    override fun setVolume(index: Int) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0)
        onChange(true)
    }

    override fun setVolumeToMinimumVolumePercent() {
        minimumVolumePercent = noiseSettings.getMinimumVolumePercent()
        val volumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        val volumeIndex = (volumeMax * minimumVolumePercent / 100).toInt() + 1
        setVolume(volumeIndex)
    }
}