package com.example.playlistmaker.media.domain

import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun addTrackToFavorites(track: Track)
    suspend fun removeTrackFromFavorites(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
}