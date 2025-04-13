package com.example.playlistmaker.search.domain

import java.util.concurrent.Executors

class SearchTracksInteractorImpl(private val repository: TracksRepository) :
    SearchTracksInteractor {
    private val executor = Executors.newSingleThreadExecutor()

    override fun searchTracks(expression: String, consumer: SearchTracksInteractor.Consumer) {
        try {
            executor.execute {
                consumer.consume(repository.searchTracks(expression))
            }
        } catch (e: Exception) {
            consumer.consume(emptyList())
        }
    }
}