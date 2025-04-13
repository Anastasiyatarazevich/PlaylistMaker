package com.example.playlistmaker.player.domain

import com.example.playlistmaker.models.Track

interface AudioPlayerInteractor {
    fun initialize(track: Track)
    fun togglePlayback()
    fun pausePlayback()
    fun releasePlayer()
    fun setProgressListener(listener: (String) -> Unit)
    fun setCompletionListener(listener: () -> Unit)
}