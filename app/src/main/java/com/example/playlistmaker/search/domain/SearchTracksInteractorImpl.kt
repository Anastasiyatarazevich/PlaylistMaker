package com.example.playlistmaker.search.domain

import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class SearchTracksInteractorImpl(private val repository: TracksRepository) :
    SearchTracksInteractor {

    override fun searchTracks(expression: String): Flow<List<Track>> =
        repository.searchTracks(expression).catch { emit(emptyList()) }

}