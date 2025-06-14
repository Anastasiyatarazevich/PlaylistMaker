package com.example.playlistmaker.search.domain

import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun addTrack(track: Track) = repository.addTrack(track)

    override fun clearHistory() = repository.clearHistory()

    override fun getHistory(): Flow<List<Track>> = repository.getHistory()
}
