package com.example.playlistmaker.player.domain

import com.example.playlistmaker.models.Track

sealed interface AudioPlayerState {
    object Loading : AudioPlayerState

    data class Content(
        val track: Track,
        val currentTime: String,
        val isPlaying: Boolean
    ) : AudioPlayerState

    data class Error(
        val errorMessage: String
    ) : AudioPlayerState
}