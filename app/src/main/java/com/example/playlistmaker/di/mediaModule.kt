package com.example.playlistmaker.di

import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.media.data.FavoritesRepositoryImpl
import com.example.playlistmaker.media.data.ImageSaverImpl
import com.example.playlistmaker.media.data.PlaylistRepositoryImpl
import com.example.playlistmaker.media.domain.FavoritesInteractor
import com.example.playlistmaker.media.domain.FavoritesInteractorImpl
import com.example.playlistmaker.media.domain.FavoritesRepository
import com.example.playlistmaker.media.domain.ImageSaver
import com.example.playlistmaker.media.domain.PlaylistInteractor
import com.example.playlistmaker.media.domain.PlaylistInteractorImpl
import com.example.playlistmaker.media.domain.PlaylistRepository
import com.example.playlistmaker.media.ui.viewmodel.CreatePlaylistViewModel
import com.example.playlistmaker.media.ui.viewmodel.FavoritesViewModel
import com.example.playlistmaker.media.ui.viewmodel.PlaylistInfoViewModel
import com.example.playlistmaker.media.ui.viewmodel.PlaylistsViewModel
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {
    viewModel { PlaylistsViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }

    single<ImageSaver> { ImageSaverImpl(androidContext()) }

    viewModel {
        CreatePlaylistViewModel(
            imageSaver = get(),
            savePlaylistInteractor = get()
        )
    }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }

    single { Gson() }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(
            dao = get<AppDatabase>().playlistDao(),
            playlistTrackDao = get<AppDatabase>().playlistTracksDao(),
            gson = get()
        )
    }

    viewModel { (playlistId: Int) ->
        PlaylistInfoViewModel(get(), playlistId)
    }

    single<PlaylistInteractor> { PlaylistInteractorImpl(get()) }
}
