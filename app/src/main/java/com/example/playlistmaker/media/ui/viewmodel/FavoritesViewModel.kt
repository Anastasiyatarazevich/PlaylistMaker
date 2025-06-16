package com.example.playlistmaker.media.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.FavoritesInteractor
import com.example.playlistmaker.media.domain.FavoritesState
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoritesState>()
    fun getState(): LiveData<FavoritesState> = stateLiveData

    init {
        loadFavorites()
    }

     fun loadFavorites() {
        viewModelScope.launch {
            favoritesInteractor.getFavorites().collect { tracks ->
                if (tracks.isEmpty()) {
                    stateLiveData.postValue(FavoritesState.Empty)
                } else {
                    stateLiveData.postValue(FavoritesState.Content(tracks))
                }
            }
        }
    }
}
