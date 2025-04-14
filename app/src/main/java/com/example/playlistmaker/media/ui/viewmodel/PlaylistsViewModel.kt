package com.example.playlistmaker.media.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.media.domain.PlaylistsState

class PlaylistsViewModel : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun getState(): LiveData<PlaylistsState> = stateLiveData

    init {
        stateLiveData.value = PlaylistsState.Empty
    }

}
