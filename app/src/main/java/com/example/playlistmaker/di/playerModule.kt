package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.player.data.AudioPlayerHelper
import com.example.playlistmaker.player.domain.AudioPlayerInteractorImpl
import com.example.playlistmaker.player.domain.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.AudioPlayerRepository
import com.example.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.get
import org.koin.dsl.module

val playerModule = module {

    factory<AudioPlayerRepository> { (track: Track) ->
        val player = get<MediaPlayer>()
        AudioPlayerHelper(track, player)
    }

    factory<AudioPlayerInteractor> { (track: Track) ->
        AudioPlayerInteractorImpl(
            repository = get<AudioPlayerRepository> { parametersOf(track) }
        )
    }

    factory { MediaPlayer() }

    viewModel { (track: Track) ->
        AudioPlayerViewModel(
            interactor = get{parametersOf(track)},
            track = track
        )
    }
}