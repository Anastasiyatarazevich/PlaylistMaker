package com.example.playlistmaker.media.domain

import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository
) : FavoritesInteractor {

    override suspend fun addToFavorites(track: Track) {
        repository.addTrackToFavorites(track)
    }

    override suspend fun removeFromFavorites(track: Track) {
        repository.removeTrackFromFavorites(track)
    }

    override fun getFavorites(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }
}
