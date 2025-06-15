package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val iTunesService: ITunesApiService
) : NetworkClient {


    override suspend fun doRequest(dto: Any): Response = withContext(Dispatchers.IO) {
        if (dto !is TracksSearchRequest) {
            Response().apply { resultCode = 400 }
        } else {
            try {
                iTunesService.searchTracks(dto.expression)
                    .apply { resultCode = 200 }
            } catch (e: Throwable) {
                Response().apply { resultCode = 500 }
            }
        }
    }
}
