package com.example.playlistmaker.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val trackId: Int,
    val collectionName: String,
    val releaseDate: String?,
    val genreName: String?,
    val country: String,
    val previewUrl: String
) : Parcelable
