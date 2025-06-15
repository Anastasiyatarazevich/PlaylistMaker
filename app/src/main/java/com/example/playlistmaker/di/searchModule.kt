package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.search.data.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.ITunesApiService
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.SearchTracksInteractor
import com.example.playlistmaker.search.domain.SearchTracksInteractorImpl
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.util.PreferenceKeys.PREFS_NAME
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchModule = module {

    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<ITunesApiService> {
        get<Retrofit>().create(ITunesApiService::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(iTunesService = get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(
            networkClient = get(),
            favoriteTrackDao = get<AppDatabase>().favoriteTrackDao()
        )
    }

    factory<SearchTracksInteractor> {
        SearchTracksInteractorImpl(get())
    }

    factory<SearchHistoryRepository> { (context: Context) ->
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        SearchHistoryRepositoryImpl(
            sharedPrefs = sharedPrefs,
            gson = get(),
            favoriteTrackDao = get<AppDatabase>().favoriteTrackDao()
        )
    }

    factory<SearchHistoryInteractor> { (context: Context) ->
        val repository = get<SearchHistoryRepository> { parametersOf(context) }
        SearchHistoryInteractorImpl(repository)
    }

    viewModel { (context: Context) ->
        SearchViewModel(
            searchTracksInteractor = get(),
            searchHistoryInteractor = get { parametersOf(context) }
        )
    }
}