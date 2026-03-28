package com.example.mathpuzzlegame

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.view.SoundEffectConstants
import android.view.View

object SoundManager {
    private var soundPool: SoundPool? = null

    fun init(context: Context) {
        if (soundPool != null) return

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()
    }

    fun playClick(context: Context) {
        // Use standard Android system click sound for the buttons
        val view = View(context)
        view.playSoundEffect(SoundEffectConstants.CLICK)
    }

    fun playSuccess(context: Context) {
        // Use the system's default notification sound as a "Success" indicator
        try {
            val notification = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
            val r = android.media.RingtoneManager.getRingtone(context, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playError(context: Context) {
        // Use a standard haptic/sound feedback for errors if possible, or another system sound
        try {
            // Using TYPE_ALARM or another sound for error placeholder
            val notification = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
            // In a real app, you'd have a specific error.wav file
            val r = android.media.RingtoneManager.getRingtone(context, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
