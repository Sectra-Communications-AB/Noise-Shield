package com.sectra.noiseshield

import java.security.SecureRandom
import kotlin.math.abs

private var b0 = 0.0
private var b1 = 0.0
private var b2 = 0.0
private var b3 = 0.0
private var b4 = 0.0
private var b5 = 0.0
private var b6 = 0.0
private val secureRandom = SecureRandom()

fun generatePinkNoise(sampleSize: Int): FloatArray {
    return generatePinkNoise(generateWhiteNoise(sampleSize))
}

private fun generatePinkNoise(whiteNoiseArray: FloatArray): FloatArray {
    val pinkNoiseArray = FloatArray(whiteNoiseArray.size)
    for (i in whiteNoiseArray.indices) {
        val white = whiteNoiseArray[i]
        b0 = 0.99886 * b0 + white * 0.0555179
        b1 = 0.99332 * b1 + white * 0.0750759
        b2 = 0.96900 * b2 + white * 0.1538520
        b3 = 0.86650 * b3 + white * 0.3104856
        b4 = 0.55000 * b4 + white * 0.5329522
        b5 = -0.7616 * b5 - white * 0.0168980
        val pink = b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362
        b6 = white * 0.115926

        pinkNoiseArray[i] = pink.toFloat()
    }
    return pinkNoiseArray.normalize()
}

private fun generateWhiteNoise(sampleSize: Int): FloatArray {
    return FloatArray(sampleSize) { whiteNoise() }
}

private fun whiteNoise(): Float {
    return (secureRandom.nextFloat() * 2) - 1
}

private fun FloatArray.normalize(): FloatArray {
    val maxAbsValue = this.maxOfOrNull { abs(it) } ?: return this
    if (maxAbsValue == 0f) return this
    return this.map { it / maxAbsValue }.toFloatArray()
}