package com.example.playlistmaker.media.domain

import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(track: Track)
    fun getFavorites(): Flow<List<Track>>
}