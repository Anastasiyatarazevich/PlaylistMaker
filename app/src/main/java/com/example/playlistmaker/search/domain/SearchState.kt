package com.example.playlistmaker.search.domain

import com.example.playlistmaker.models.Track

sealed interface SearchState {
    object Loading : SearchState
    data class Content(val tracks: List<Track>) : SearchState
    object Empty : SearchState
    object Start : SearchState
}