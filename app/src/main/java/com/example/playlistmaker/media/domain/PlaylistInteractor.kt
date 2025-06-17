package com.example.playlistmaker.media.domain

import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(name: String, description: String?, imagePath: String?)
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
}