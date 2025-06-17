package com.example.playlistmaker.di

import androidx.room.Room
import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.db.TrackDbConverter
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "playlist_database.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().favoriteTrackDao() }

    factory { TrackDbConverter() }
}