package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient (): NetworkClient {

    private val iTunesBaseUrl = "https://itunes.apple.com/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService: ITunesApiService = retrofit.create(ITunesApiService::class.java)

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
