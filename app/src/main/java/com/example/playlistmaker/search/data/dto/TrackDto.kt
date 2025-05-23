package com.example.playlistmaker.search.data.dto

data class TrackDto(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val trackId: Int,
    val collectionName: String,
    val releaseDate: String?,
    val genreName: String,
    val country: String,
    val previewUrl: String
)