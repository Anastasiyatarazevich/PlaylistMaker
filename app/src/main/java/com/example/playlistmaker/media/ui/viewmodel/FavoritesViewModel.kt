package com.example.playlistmaker.media.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.media.domain.FavoritesState

class FavoritesViewModel : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoritesState>()
    fun getState(): LiveData<FavoritesState> = stateLiveData

    init {
        stateLiveData.value = FavoritesState.Empty
    }
}
