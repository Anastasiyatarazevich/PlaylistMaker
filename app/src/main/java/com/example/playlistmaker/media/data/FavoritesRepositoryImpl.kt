package com.example.playlistmaker.media.data

import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.db.TrackDbConverter
import com.example.playlistmaker.media.domain.FavoritesRepository
import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val converter: TrackDbConverter
) : FavoritesRepository {

    override suspend fun addTrackToFavorites(track: Track) {
        val entity = converter.map(track)
        appDatabase.favoriteTrackDao().insertTrack(entity)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        val entity = converter.map(track)
        appDatabase.favoriteTrackDao().deleteTrack(entity)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> =
        appDatabase.favoriteTrackDao().getAllTracks()
            .map { list ->
                list.sortedByDescending { it.addedTimestamp }
                    .map { converter.map(it) }
            }
}