package com.example.playlistmaker.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE playlistId = :id")
    suspend fun getPlaylistById(id: Int): PlaylistEntity?

    @Query("SELECT * FROM playlists")
    suspend fun getAllPlaylistsOnce(): List<PlaylistEntity>

    @Query("DELETE FROM playlists WHERE playlistId = :id")
    suspend fun deletePlaylistById(id: Int)

    @Query("SELECT trackIdsJson FROM playlists")
    fun getAllTrackIdsJsonFlow(): Flow<List<String>>
}