package com.example.playlistmaker.search.data.impl

import android.content.SharedPreferences
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(private val sharedPrefs: SharedPreferences) :
    SearchHistoryRepository {

    override fun addTrack(track: Track) {
        val list = getHistory().toMutableList()
        list.removeAll { it.trackId == track.trackId }
        list.add(0, track)
        if (list.size > MAX_HISTORY_SIZE) {
            list.removeAt(list.lastIndex)
        }
        saveHistory(list)
    }


    override fun getHistory(): List<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY_KEY, null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<Track>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }

    override fun clearHistory() {
        sharedPrefs.edit().remove(SEARCH_HISTORY_KEY).apply()
    }

    private fun saveHistory(list: List<Track>) {
        val json = Gson().toJson(list)
        sharedPrefs.edit().putString(SEARCH_HISTORY_KEY, json).apply()
    }

    companion object {
        private const val SEARCH_HISTORY_KEY = "HISTORY_KEY"
        private const val MAX_HISTORY_SIZE = 10
    }
}