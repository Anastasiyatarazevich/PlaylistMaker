package com.example.playlistmaker.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: FavoriteTrackEntity)

    @Delete
    suspend fun deleteTrack(track: FavoriteTrackEntity)

    @Query("SELECT * FROM favorite_tracks ORDER BY addedTimestamp DESC")
    fun getAllTracks(): Flow<List<FavoriteTrackEntity>>

    @Query("SELECT trackId FROM favorite_tracks")
    suspend fun getAllTrackIds(): List<Int>
}