package com.example.playlistmaker.media.domain

sealed interface PlaylistsState {
    object Empty : PlaylistsState
}