package com.example.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.SearchState
import com.example.playlistmaker.search.domain.SearchTracksInteractor

class SearchViewModel(
    private val searchTracksInteractor: SearchTracksInteractor,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val screenState = MutableLiveData<SearchState>()
    fun getScreenState(): LiveData<SearchState> = screenState

    private val history = MutableLiveData<List<Track>>()
    fun getHistory(): LiveData<List<Track>> = history

    init {
        screenState.value = SearchState.Start
        history.value = searchHistoryRepository.getHistory()
    }

    fun searchTracks(query: String) {
        screenState.value = SearchState.Loading
        searchTracksInteractor.searchTracks(query, object : SearchTracksInteractor.Consumer {
            override fun consume(tracks: List<Track>) {
                if (tracks.isEmpty()) {
                    screenState.postValue(SearchState.Empty)
                } else {
                    screenState.postValue(SearchState.Content(tracks))
                }
            }
        })
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryRepository.addTrack(track)
        history.value = searchHistoryRepository.getHistory()
    }

    companion object {
        fun getViewModelFactory(
            searchTracksInteractor: SearchTracksInteractor,
            searchHistoryRepository: SearchHistoryRepository
        ): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(searchTracksInteractor, searchHistoryRepository) as T
                }
            }
    }
}