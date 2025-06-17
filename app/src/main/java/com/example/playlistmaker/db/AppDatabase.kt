package com.example.playlistmaker.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 3, entities = [FavoriteTrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): FavoriteTrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTracksDao(): PlaylistTracksDao
}