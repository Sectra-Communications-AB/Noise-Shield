package com.sectra.noiseshield.volumecontroller

interface IVolumeController {
    var volumePercent: Float
    var volumeToLow: Boolean
    fun getVolumeIndex() : Int
    fun setVolumeToMinimumVolumePercent()
    fun setVolume(index: Int)
    fun updateVolumeToLow()
}