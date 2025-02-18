package com.sectra.noiseshield.player

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sectra.noiseshield.generatePinkNoise
import com.sectra.noiseshield.volumecontroller.IVolumeController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NoisePlayer(
    private val coroutineScope: CoroutineScope,
    private var volumeController: IVolumeController,
) : IPlayer {

    override var isPlaying by mutableStateOf(false)
    private var isGenerating = true

    private var audioTrack: AudioTrack? = null
    private var originalVolume: Int = -1

    //TODO examine this what we want, more or less quality or is this perfect
    private val sampleRate = 44100 // Standard CD quality
    private val numSamples = sampleRate

    private val audioDataQueue = ArrayDeque<FloatArray>()
    private var producerJob: Job? = null
    private var consumerJob: Job? = null

    init {
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_FLOAT
        )
        audioTrack = AudioTrack.Builder().setAudioAttributes(
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        ).setAudioFormat(
            AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                .setSampleRate(sampleRate).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build()
        ).setBufferSizeInBytes(minBufferSize).setTransferMode(AudioTrack.MODE_STREAM).build()

        producerJob = coroutineScope.launch(Dispatchers.Default) {
            while (isGenerating) {
                synchronized(audioDataQueue) {
                    if (audioDataQueue.size < 10) { // Keep a maximum of 10 chunks in the queue
                        audioDataQueue.add(generatePinkNoise(numSamples))
                    }
                }
                delay(50) // Adjust based on how fast you generate/consume
            }
        }
    }

    override fun toggle() {
        if (isPlaying) {
            restoreVolume()
            stopPlaying()
        } else {
            increaseVolume()
            startPlaying()
        }
    }

    fun restoreVolume() {
        if (originalVolume != -1) {
            volumeController.setVolume(originalVolume)
            originalVolume = -1
        }
    }

    private fun increaseVolume() {
        if (volumeController.volumeToLow) {
            originalVolume = volumeController.getVolumeIndex()
            volumeController.setVolumeToMinimumVolumePercent()
        }
    }

    private fun startPlaying() {
        if (isPlaying) return
        isPlaying = true

        consumerJob = coroutineScope.launch(Dispatchers.Default) {
            audioTrack?.play()
            while (isPlaying) {
                val audioData = synchronized(audioDataQueue) {
                    if (audioDataQueue.isNotEmpty()) audioDataQueue.removeFirst() else null
                }
                audioData?.let {
                    audioTrack?.write(it, 0, it.size, AudioTrack.WRITE_BLOCKING)
                }
            }
        }
    }

    private fun stopPlaying() {
        if (!isPlaying) return
        isPlaying = false
        consumerJob?.cancel()
        audioTrack?.stop()
    }

    fun destroy() {
        isGenerating = false
        producerJob?.cancel()
        audioDataQueue.clear()
        audioTrack?.release()
    }
}