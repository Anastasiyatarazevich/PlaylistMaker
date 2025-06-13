package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.search.data.dto.TracksResponse
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.search.data.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Flow<List<Track>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        val list = if (response.resultCode == 200 && response is TracksResponse ) {
            response.results.map {
                Track(
                    trackName = it.trackName,
                    artistName = it.artistName,
                    trackTimeMillis = it.trackTimeMillis,
                    artworkUrl100 = it.artworkUrl100,
                    trackId = it.trackId,
                    collectionName = it.collectionName,
                    releaseDate = it.releaseDate,
                    genreName = it.genreName,
                    country = it.country,
                    previewUrl = it.previewUrl
                )
            }
        } else {
            emptyList()
        }
        emit(list)
    }.flowOn(Dispatchers.IO)
}