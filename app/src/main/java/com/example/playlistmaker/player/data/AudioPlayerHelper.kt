package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.player.domain.AudioPlayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerHelper(
    private val track: Track,
    private val mediaPlayer: MediaPlayer
) : AudioPlayerRepository {
    override var playerState = STATE_DEFAULT
    private var progressJob: Job? = null

    private var onProgressUpdate: ((String) -> Unit)? = null
    private var onCompletion: (() -> Unit)? = null

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
                    progressJob?.cancel()
                    onProgressUpdate?.invoke("00:00")
                    onCompletion?.invoke()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun startPlayer() {
        if (playerState == STATE_PREPARED || playerState == STATE_PAUSED) {
            if (mediaPlayer.currentPosition >= mediaPlayer.duration) {
                mediaPlayer.seekTo(0)
            }
            mediaPlayer.start()
            playerState = STATE_PLAYING
            startProgressLoop()
        }
    }

    override  fun pausePlayer() {
        if (playerState == STATE_PLAYING) {
            mediaPlayer.pause()
            playerState = STATE_PAUSED
            progressJob?.cancel()
        }
    }

  override  fun release() {
        progressJob?.cancel()
        mediaPlayer.release()
    }

    override fun setProgressListener(listener: (String) -> Unit) {
        onProgressUpdate = listener
    }

    override fun setCompletionListener(listener: () -> Unit) {
        onCompletion = listener
    }

    private fun startProgressLoop() {
        progressJob?.cancel()
        progressJob = CoroutineScope(Dispatchers.Main).launch {
            while (playerState == STATE_PLAYING) {
                val currentTime = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(mediaPlayer.currentPosition)
                onProgressUpdate?.invoke(currentTime)
                delay(UPDATE_INTERVAL)
            }
        }
    }

    companion object {
        private const val UPDATE_INTERVAL = 300L
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }
}