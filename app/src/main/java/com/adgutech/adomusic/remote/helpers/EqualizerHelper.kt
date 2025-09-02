/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.adgutech.adomusic.remote.helpers

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.audiofx.Equalizer
import com.adgutech.adomusic.remote.extensions.logE
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.commons.extensions.showToast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

object EqualizerHelper {

    lateinit var instance: Equalizer
        internal set

    const val EQUALIZER_PRESET_CUSTOM = -1

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private val sampleRate = AudioTrack
        .getNativeOutputSampleRate(AudioManager.STREAM_MUSIC)
    private val channelConfig = AudioFormat.CHANNEL_OUT_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioTrack
        .getMinBufferSize(
            sampleRate,
            channelConfig,
            audioFormat
        )

    private val audioTrack = AudioTrack(
        audioAttributes,
        AudioFormat.Builder()
            .setSampleRate(sampleRate)
            .setChannelMask(channelConfig)
            .setEncoding(audioFormat)
            .build(),
        bufferSize,
        AudioTrack.MODE_STATIC, // Or MODE_STREAM
        AudioManager.AUDIO_SESSION_ID_GENERATE // Generate a new session ID
    )

    private var globalAudioSessionId = 0

    fun setupEqualizer(context: Context) {
        try {
            instance = try {
                Equalizer(0, globalAudioSessionId)
            } catch (e: Exception) {
                Equalizer(0, audioTrack.audioSessionId)
            }
            val preset = context.preference.equalizerPreset
            instance.enabled = context.preference.isEqualizerEnabled
            if (preset != EQUALIZER_PRESET_CUSTOM) {
                instance.usePreset(preset.toShort())
            } else {
                val minValue = instance.bandLevelRange[0]
                val bandType = object : TypeToken<HashMap<Short, Int>>() {}.type
                val equalizerBands = Gson().fromJson<HashMap<Short, Int>>(
                    context.preference.equalizerBands,
                    bandType
                ) ?: java.util.HashMap()

                for ((key, value) in equalizerBands) {
                    val newValue = value + minValue
                    if (instance.getBandLevel(key) != newValue.toShort()) {
                        instance.setBandLevel(key, newValue.toShort())
                    }
                }
            }
        } catch (error: ExceptionInInitializerError) {
            context.showToast("Error initializing Equalizer")
            logE("Error initializing Equalizer: $error")
        }
    }

    // copy-pasted from the file size formatter, should be simplified
    fun setFormatFrequency(value: Double): String {
        if (value <= 0) {
            return "0 Hz"
        }
        val units = arrayOf("Hz", "kHz", "gHz")
        val digitGroups = (log10(value) / log10(1000.0)).toInt()
        return "${DecimalFormat("#,##0.#").format(value / 1000.0.pow(digitGroups.toDouble()))} ${units[digitGroups]}"
    }

    fun release() {
        if (::instance.isInitialized) {
            instance.release()
        }
    }
}