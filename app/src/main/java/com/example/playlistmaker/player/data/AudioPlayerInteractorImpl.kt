package com.example.playlistmaker.player.data

import com.example.playlistmaker.player.domain.AudioPlayerInteractor
import com.example.playlistmaker.models.Track
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin

class AudioPlayerInteractorImpl() : AudioPlayerInteractor {

    private var audioPlayerHelper: AudioPlayerHelper? = null

    override fun initialize(track: Track) {
        audioPlayerHelper = getKoin().get<AudioPlayerHelper> { parametersOf(track) }
    }

    override fun togglePlayback() {
        audioPlayerHelper?.let { controller ->
            when (controller.playerState) {
                AudioPlayerHelper.STATE_PLAYING -> controller.pausePlayer()
                AudioPlayerHelper.STATE_PREPARED, AudioPlayerHelper.STATE_PAUSED -> controller.startPlayer()
            }
        }
    }

    override fun pausePlayback() {
        audioPlayerHelper?.pausePlayer()
    }

    override fun releasePlayer() {
        audioPlayerHelper?.release()
    }

    override fun setProgressListener(listener: (String) -> Unit) {
        audioPlayerHelper?.onProgressUpdate = listener
    }

    override fun setCompletionListener(listener: () -> Unit) {
        audioPlayerHelper?.onCompletion = listener
    }
}