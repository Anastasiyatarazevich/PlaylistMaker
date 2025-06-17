package com.example.playlistmaker.media.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.PlaylistInteractor
import com.example.playlistmaker.media.domain.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun getState(): LiveData<PlaylistsState> = stateLiveData

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists()
                .collect { playlists ->
                    if (playlists.isEmpty()) {
                        stateLiveData.postValue(PlaylistsState.Empty)
                    } else {
                        stateLiveData.postValue(PlaylistsState.Content(playlists))
                    }
                }
        }
    }
}
