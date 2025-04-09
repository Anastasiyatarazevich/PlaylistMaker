package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchTracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import java.util.concurrent.Executors

class SearchTracksInteractorImpl(private val repository: TracksRepository) :
    SearchTracksInteractor {
    private val executor = Executors.newSingleThreadExecutor()

    override fun searchTracks(expression: String, consumer: SearchTracksInteractor.Consumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(expression))
        }
    }
}