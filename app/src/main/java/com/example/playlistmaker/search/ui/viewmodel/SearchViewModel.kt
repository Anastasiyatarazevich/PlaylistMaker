package com.example.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.SearchState
import com.example.playlistmaker.search.domain.SearchTracksInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class SearchViewModel(
    private val searchTracksInteractor: SearchTracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _screenState = MutableStateFlow<SearchState>(SearchState.Start)
    val screenState: StateFlow<SearchState> = _screenState

    private var searchJob: Job? = null

    private val history = MutableLiveData<List<Track>>()
    fun getHistory(): LiveData<List<Track>> = history

    init {
        viewModelScope.launch {
            searchHistoryInteractor.getHistory().collect {
                history.value = it
            }
        }
    }

    fun performSearchDebounced(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchTracksInteractor.searchTracks(query)
                .map { tracks ->
                    if (tracks.isEmpty()) SearchState.Empty else SearchState.Content(tracks)
                }
                .onStart { emit(SearchState.Loading) }
                .collect { state ->
                    _screenState.value = state
                }
        }
        _screenState.value = SearchState.Loading
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrack(track)
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
    }

    fun resetToInitialState() {
        _screenState.value = SearchState.Start
    }
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}