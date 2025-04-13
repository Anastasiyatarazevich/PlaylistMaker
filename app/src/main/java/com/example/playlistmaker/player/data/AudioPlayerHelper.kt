package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerHelper(private val track: Track) {
    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    var playerState = STATE_DEFAULT
        private set

    var onProgressUpdate: ((String) -> Unit)? = null
    var onCompletion: (() -> Unit)? = null

    init {
        preparePlayer()
    }

    private fun preparePlayer() {
        val previewUrl = track.previewUrl
        if (previewUrl.isNotEmpty()) {
            try {
                mediaPlayer.setDataSource(previewUrl)
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener {
                    playerState = STATE_PREPARED
                }
                mediaPlayer.setOnCompletionListener {
                    playerState = STATE_PREPARED
                    mediaPlayer.seekTo(0)
                    handler.removeCallbacksAndMessages(null)
                    onProgressUpdate?.invoke("00:00")
                    onCompletion?.invoke()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val progressRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                val currentTime = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(mediaPlayer.currentPosition)
                onProgressUpdate?.invoke(currentTime)
                handler.postDelayed(this, UPDATE_INTERVAL)
            }
        }
    }

    fun startPlayer() {
        if (playerState == STATE_PREPARED || playerState == STATE_PAUSED) {
            if (mediaPlayer.currentPosition >= mediaPlayer.duration) {
                mediaPlayer.seekTo(0)
            }
            mediaPlayer.start()
            playerState = STATE_PLAYING
            handler.post(progressRunnable)
        }
    }

    fun pausePlayer() {
        if (playerState == STATE_PLAYING) {
            mediaPlayer.pause()
            playerState = STATE_PAUSED
            handler.removeCallbacks(progressRunnable)
        }
    }

    fun release() {
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }

    companion object {
        private const val UPDATE_INTERVAL = 300L
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }
}