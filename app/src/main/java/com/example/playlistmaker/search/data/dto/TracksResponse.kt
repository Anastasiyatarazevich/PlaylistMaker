package com.example.playlistmaker.search.data.dto

data class TracksResponse(
    val resultCount: Int,
    val results: List<TrackDto>
): Response()