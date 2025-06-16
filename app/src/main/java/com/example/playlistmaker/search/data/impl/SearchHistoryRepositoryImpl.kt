package com.example.playlistmaker.search.data.impl

import android.content.SharedPreferences
import com.example.playlistmaker.db.FavoriteTrackDao
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SearchHistoryRepositoryImpl(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson,
    private val favoriteTrackDao: FavoriteTrackDao
) :
    SearchHistoryRepository {

    private val historyFlow = MutableStateFlow<List<Track>>(emptyList())

    init {
        reloadHistory()
    }

    private fun reloadHistory() {
        val rawHistory = loadFromPrefs()
        GlobalScope.launch(Dispatchers.IO) {
            val favoriteIds = favoriteTrackDao.getAllTrackIds()
            val updated = rawHistory.map { it.copy(isFavorite = favoriteIds.contains(it.trackId)) }
            historyFlow.emit(updated)
        }

    }

    override fun addTrack(track: Track) {
        val list = historyFlow.value.toMutableList()
        list.removeAll { it.trackId == track.trackId }
        list.add(0, track)
        if (list.size > MAX_HISTORY_SIZE) {
            list.removeAt(list.lastIndex)
        }
        saveHistory(list)
        historyFlow.value = list
        reloadHistory()
    }

    override fun getHistory(): Flow<List<Track>> = historyFlow


    private fun loadFromPrefs(): List<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY_KEY, null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    override fun clearHistory() {
        sharedPrefs.edit().remove(SEARCH_HISTORY_KEY).apply()
        historyFlow.value = emptyList()
        reloadHistory()
    }

    private fun saveHistory(list: List<Track>) {
        val json = gson.toJson(list)
        sharedPrefs.edit().putString(SEARCH_HISTORY_KEY, json).apply()
    }

    companion object {
        private const val SEARCH_HISTORY_KEY = "HISTORY_KEY"
        private const val MAX_HISTORY_SIZE = 10
    }
}