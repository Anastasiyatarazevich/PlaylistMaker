package com.example.playlistmaker.di

import com.example.playlistmaker.models.Track
import com.example.playlistmaker.player.data.AudioPlayerInteractorImpl
import com.example.playlistmaker.player.domain.AudioPlayerInteractor
import com.example.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    factory<AudioPlayerInteractor> {
        AudioPlayerInteractorImpl()
    }

    viewModel { (track: Track) ->
        AudioPlayerViewModel(get(), track)
    }
}