package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.player.data.AudioPlayerHelper
import com.example.playlistmaker.player.data.AudioPlayerInteractorImpl
import com.example.playlistmaker.player.domain.AudioPlayerInteractor
import com.example.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    factory<AudioPlayerInteractor> {
        AudioPlayerInteractorImpl()
    }

    factory { MediaPlayer() }

    factory { (track: Track) ->
        val player = get<MediaPlayer>()
        AudioPlayerHelper(track, player)
    }

    viewModel { (track: Track) ->
        AudioPlayerViewModel(
            interactor = get(),
            track = track
        )
    }
}