package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchTracksInteractor {
    fun searchTracks(expression: String, consumer: Consumer)

    interface Consumer {
        fun consume(tracks: List<Track>)
    }
}