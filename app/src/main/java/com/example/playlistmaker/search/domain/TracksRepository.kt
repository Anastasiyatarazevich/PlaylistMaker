package com.example.playlistmaker.search.domain

import com.example.playlistmaker.models.Track

interface TracksRepository {
    fun searchTracks(expression: String): List<Track>
}

