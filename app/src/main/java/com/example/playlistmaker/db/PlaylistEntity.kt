package com.example.playlistmaker.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val playlistId: Int = 0,
    val name: String,
    val description: String?,
    val imagePath: String?,
    val trackIdsJson: String,
    val trackCount: Int
)