package com.example.playlistmaker.media.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.PlaylistInteractor
import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.models.Track
import kotlinx.coroutines.launch

class PlaylistInfoViewModel(
   private val playlistInteractor: PlaylistInteractor,
    playlistId: Int
) : ViewModel() {

    private val currentPlaylistId = playlistId

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    private val _durationMinutes = MutableLiveData<Int>()
    val durationMinutes: LiveData<Int> = _durationMinutes

    private val _tracksCount = MutableLiveData<Int>()
    val tracksCount: LiveData<Int> = _tracksCount

    private val _playlistDeleted = MutableLiveData<Unit>()
    val playlistDeleted: LiveData<Unit> = _playlistDeleted

    private val _shouldReloadData = MutableLiveData(false)
    val shouldReloadData: LiveData<Boolean> = _shouldReloadData

    init {
        loadData()
    }

    fun refreshData() {
        _shouldReloadData.value = true
    }

    fun reloadIfNeeded() {
        if (_shouldReloadData.value == true) {
            loadData()
            _shouldReloadData.value = false
        }
    }

    fun removeTrackFromPlaylist(track: Track) {
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(track, currentPlaylistId)
            loadData()
        }
    }

    fun deleteCurrentPlaylist() {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(currentPlaylistId)
            _playlistDeleted.postValue(Unit)
        }
    }

    fun loadData() {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(currentPlaylistId)
                .collect { pl ->
                    _playlist.postValue(pl)
                }
            playlistInteractor.getTracksByPlaylist(currentPlaylistId)
                .collect { tracks ->
                    _tracks.postValue(tracks)
                    val totalMs = tracks.sumOf { it.trackTimeMillis }
                    val mins = (totalMs / 1000 / 60).toInt()
                    _durationMinutes.postValue(mins)
                    _tracksCount.postValue(tracks.size)
                }
        }
    }
}