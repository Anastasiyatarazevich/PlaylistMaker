package com.example.playlistmaker.search.domain

import com.example.playlistmaker.models.Track

interface SearchTracksInteractor {
    fun searchTracks(expression: String, consumer: Consumer)

    interface Consumer {
        fun consume(tracks: List<Track>)
    }
}