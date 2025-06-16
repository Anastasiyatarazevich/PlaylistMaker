package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.FavoritesInteractor
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.player.domain.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.AudioPlayerState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val interactor: AudioPlayerInteractor,
    private val track: Track,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val screenState = MutableLiveData<AudioPlayerState>()

    private val isFavoriteLiveData = MutableLiveData<Boolean>()
    fun isFavorite(): LiveData<Boolean> = isFavoriteLiveData

    fun getThemeSettings(): LiveData<AudioPlayerState> = screenState

    init {
        screenState.value = AudioPlayerState.Loading

        interactor.setProgressListener { currentTime ->
            val currentState = screenState.value
            screenState.value = if (currentState is AudioPlayerState.Content) {
                currentState.copy(currentTime = currentTime)
            } else {
                AudioPlayerState.Content(track!!, currentTime, isPlaying = true)
            }
        }
        interactor.setCompletionListener {
            screenState.value = AudioPlayerState.Content(track, TIME_PLAY_TRACK, isPlaying = false)
        }

        viewModelScope.launch {
            val favorites = favoritesInteractor.getFavorites().first()
            val isFavorite = favorites.any { it.trackId == track.trackId }
            track.isFavorite = isFavorite
            isFavoriteLiveData.postValue(isFavorite)

            screenState.postValue(AudioPlayerState.Content(track, TIME_PLAY_TRACK, isPlaying = false))
        }

        screenState.value = AudioPlayerState.Content(track, TIME_PLAY_TRACK, isPlaying = false)
    }

    fun togglePlayback() {
        interactor.togglePlayback()
        val currentState = screenState.value
        if (currentState is AudioPlayerState.Content) {
            screenState.value = currentState.copy(isPlaying = !currentState.isPlaying)
        }
    }

    fun pausePlayback() {
        interactor.pausePlayback()
        val currentState = screenState.value
        if (currentState is AudioPlayerState.Content) {
            screenState.value = currentState.copy(isPlaying = false)
        }
    }

    fun releasePlayer() {
        interactor.releasePlayer()
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            if (track.isFavorite) {
                favoritesInteractor.removeFromFavorites(track)
            } else {
                favoritesInteractor.addToFavorites(track)
            }

            track.isFavorite = !track.isFavorite
            isFavoriteLiveData.postValue(track.isFavorite)
        }
    }

    companion object {
        private const val TIME_PLAY_TRACK = "0:30"
    }
}