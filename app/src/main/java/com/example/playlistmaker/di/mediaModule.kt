package com.example.playlistmaker.di

import com.example.playlistmaker.media.ui.viewmodel.FavoritesViewModel
import com.example.playlistmaker.media.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {
    viewModel { PlaylistsViewModel() }
    viewModel { FavoritesViewModel() }
}
