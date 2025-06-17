package com.example.playlistmaker.media.domain

import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun createPlaylist(
        name: String,
        description: String?,
        imagePath: String?
    ) {
        repository.createPlaylist(name, description, imagePath)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        repository.addTrackToPlaylist(track, playlist)
    }
}