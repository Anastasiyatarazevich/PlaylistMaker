package com.example.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.adapters.TrackAdapter
import com.example.playlistmaker.domain.api.SearchTracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.presentation.Creator
import com.example.playlistmaker.ui.audio_player.AudioPlayerActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var clearButton: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var nothingPlaceHolder: LinearLayout
    private lateinit var noConnectionPlaceholder: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchHistoryLayout: ScrollView
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var searchHistoryClearButton: Button
    private lateinit var buttonUpdate: Button
    private var trackList = ArrayList<Track>()
    private var trackListSearchHistory = ArrayList<Track>()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter

    private lateinit var searchHistoryRepository: SearchHistoryRepository

    private lateinit var searchTracksInteractor: SearchTracksInteractor

    private var searchText: String? = null
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {
        val query = searchInput.text.toString()
        if (query.isNotEmpty()) {
            searchTracks(query)
        }
    }
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    private var isClickAllowed = true
    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
            isClickAllowed = false
        }
        return current
    }

    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val back = findViewById<MaterialToolbar>(R.id.back_button)
        searchInput = findViewById(R.id.search_input)
        clearButton = findViewById(R.id.clear_button)
        progressBar = findViewById(R.id.progress_bar)
        nothingPlaceHolder = findViewById(R.id.placeholder_nothing)
        noConnectionPlaceholder = findViewById(R.id.placeholder_no_connection)
        recyclerView = findViewById(R.id.recycler_view)
        searchHistoryLayout = findViewById(R.id.search_history_layout)
        searchHistoryRecyclerView = findViewById(R.id.search_history_recycler_view)
        searchHistoryClearButton = findViewById(R.id.clear_history_button)
        buttonUpdate = findViewById(R.id.update)

        trackAdapter = TrackAdapter(trackList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        trackHistoryAdapter = TrackAdapter(trackListSearchHistory)
        searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        searchHistoryRecyclerView.adapter = trackHistoryAdapter

        searchHistoryRepository = Creator.getSearchHistoryRepository(this)
        searchTracksInteractor = Creator.provideSearchTracksInteractor()

        trackAdapter.onClickTrack = { track ->
            if (clickDebounce()) {
                searchHistoryRepository.addTrack(track)
                updateSearchHistory()
                goToInfo(track)
            }
        }
        trackHistoryAdapter.onClickTrack = { track ->
            if (clickDebounce()) {
                searchHistoryRepository.addTrack(track)
                updateSearchHistory()
                goToInfo(track)
            }
        }

        searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchInput.text.isNullOrEmpty() && searchHistoryRepository.getHistory().isNotEmpty()) {
                searchHistoryLayout.isVisible = true
                updateSearchHistory()
            } else {
                searchHistoryLayout.isVisible = false
            }
        }

        searchHistoryClearButton.setOnClickListener {
            searchHistoryRepository.clearHistory()
            updateSearchHistory()
            searchHistoryLayout.isVisible = false
        }

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY)
            searchInput.setText(searchText)
        }

        back.setNavigationOnClickListener { finish() }

        buttonUpdate.setOnClickListener {
            val query = searchInput.text.toString()
            if (query.isNotEmpty()) {
                searchTracks(query)
            }
        }

        clearButton.setOnClickListener {
            searchInput.setText("")
            hideKeyboard()
            nothingPlaceHolder.isVisible = false
            noConnectionPlaceholder.isVisible = false
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            recyclerView.visibility = View.GONE
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (searchInput.hasFocus() && s.isNullOrEmpty() && searchHistoryRepository.getHistory().isNotEmpty()) {
                    searchHistoryLayout.isVisible = true
                    updateSearchHistory()
                } else {
                    searchHistoryLayout.isVisible = false
                    trackList.clear()
                    trackAdapter.notifyDataSetChanged()
                }
                if (!s.isNullOrEmpty()) {
                    searchDebounce()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchInput.text.toString()
                if (query.isNotEmpty()) {
                    searchTracks(query)
                }
                true
            } else false
        }
    }

    private fun goToInfo(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra(TRACK, Gson().toJson(track))
        startActivity(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateSearchHistory() {
        trackListSearchHistory.clear()
        trackListSearchHistory.addAll(searchHistoryRepository.getHistory())
        trackHistoryAdapter.notifyDataSetChanged()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchInput.windowToken, 0)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun searchTracks(term: String) {
        progressBar.visibility = View.VISIBLE
        searchTracksInteractor.searchTracks(term, object : SearchTracksInteractor.Consumer {
            override fun consume(tracks: List<Track>) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    if (tracks.isNotEmpty()) {
                        trackList.clear()
                        trackList.addAll(tracks)
                        trackAdapter.notifyDataSetChanged()
                        recyclerView.visibility = View.VISIBLE
                        nothingPlaceHolder.visibility = View.GONE
                        noConnectionPlaceholder.visibility = View.GONE
                    } else {
                        trackList.clear()
                        trackAdapter.notifyDataSetChanged()
                        recyclerView.visibility = View.GONE
                        nothingPlaceHolder.visibility = View.VISIBLE
                        noConnectionPlaceholder.visibility = View.GONE
                    }
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(SEARCH_TEXT_KEY)
        searchInput.setText(restoredText)
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "TEXT_KEY"
        private const val TRACK = "TRACK_DATA"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}