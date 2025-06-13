package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.data.AudioPlayerHelper


class AudioPlayerInteractorImpl(
    private val repository: AudioPlayerRepository
) : AudioPlayerInteractor {

    override fun togglePlayback() {
        repository?.let { controller ->
            when (controller.playerState) {
                AudioPlayerHelper.STATE_PLAYING -> controller.pausePlayer()
                AudioPlayerHelper.STATE_PREPARED, AudioPlayerHelper.STATE_PAUSED -> controller.startPlayer()
            }
        }
    }

    override fun pausePlayback() {
        repository?.pausePlayer()
    }

    override fun releasePlayer() {
        repository?.release()
    }

    override fun setProgressListener(listener: (String) -> Unit) {
        repository.setProgressListener(listener)
    }

    override fun setCompletionListener(listener: () -> Unit) {
        repository.setCompletionListener(listener)
    }
}