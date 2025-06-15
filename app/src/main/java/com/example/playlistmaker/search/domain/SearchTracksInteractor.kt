package com.example.playlistmaker.search.domain

import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchTracksInteractor {
    fun searchTracks(expression: String): Flow<List<Track>>

}