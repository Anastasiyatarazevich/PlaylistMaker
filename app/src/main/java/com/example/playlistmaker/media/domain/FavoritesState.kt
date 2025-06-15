package com.example.playlistmaker.media.domain

import com.example.playlistmaker.models.Track

sealed class FavoritesState {
    data object Empty : FavoritesState()
    data class Content(val tracks: List<Track>) : FavoritesState()
}