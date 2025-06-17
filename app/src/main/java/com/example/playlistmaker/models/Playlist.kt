package com.example.playlistmaker.models

data class Playlist(
    val id: Int,
    val name: String,
    val description: String?,
    val imagePath: String?,
    val trackIds: List<Int>,
    val trackCount: Int
)