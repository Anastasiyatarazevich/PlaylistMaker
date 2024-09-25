package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    private val trackHistoryList = getHistory().toMutableList()

    companion object {
        private const val SEARCH_HISTORY_KEY = "history_key"
        private const val MAX_HISTORY_SIZE = 10
    }

    fun addTrack(track: Track) {
        trackHistoryList.removeAll { it.trackId == track.trackId }
        trackHistoryList.add(0, track)
        if (trackHistoryList.size > MAX_HISTORY_SIZE) {
            trackHistoryList.removeLast()
        }
        saveTrack()
    }


    fun saveTrack() {
        val json = Gson().toJson(trackHistoryList)
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }


    fun getHistory(): List<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY_KEY, null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<Track>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun clearHistory() {
        trackHistoryList.clear()
        sharedPrefs.edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()
    }
}