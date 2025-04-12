package com.example.playlistmaker.search.ui

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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.player.ui.AudioPlayerActivity
import com.example.playlistmaker.search.domain.SearchState
import com.example.playlistmaker.search.ui.adapters.TrackAdapter
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.util.Creator
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel

    private var trackList = ArrayList<Track>()
    private var trackHistoryList = ArrayList<Track>()

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {
        val query = binding.searchInput.text.toString()
        if (query.isNotEmpty()) {
            viewModel.searchTracks(query)
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        trackAdapter = TrackAdapter(trackList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = trackAdapter

        trackHistoryAdapter = TrackAdapter(trackHistoryList)
        binding.searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.searchHistoryRecyclerView.adapter = trackHistoryAdapter

        val searchInteractor = Creator.provideSearchTracksInteractor()
        val historyRepository = Creator.getSearchHistoryRepository(this)
        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory(searchInteractor, historyRepository)
        ).get(SearchViewModel::class.java)

        viewModel.getScreenState().observe(this) { state ->
            when (state) {
                is SearchState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    binding.placeholderNothing.visibility = View.GONE
                }

                is SearchState.Content -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    trackList.clear()
                    trackList.addAll(state.tracks)
                    trackAdapter.notifyDataSetChanged()
                    binding.placeholderNothing.visibility = View.GONE
                }

                is SearchState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                    binding.placeholderNothing.visibility = View.VISIBLE
                }

                is SearchState.Start -> {
                    binding.progressBar.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                    binding.placeholderNothing.visibility = View.GONE
                }

            }
        }

        viewModel.getHistory().observe(this) { history ->
            trackHistoryList.clear()
            trackHistoryList.addAll(history)
            trackHistoryAdapter.notifyDataSetChanged()
        }

        trackAdapter.onClickTrack = { track ->
            if (clickDebounce()) {
                viewModel.addTrackToHistory(track)
                goToAudioPlayer(track)
            }
        }

        trackHistoryAdapter.onClickTrack = { track ->
            if (clickDebounce()) {
                viewModel.addTrackToHistory(track)
                goToAudioPlayer(track)
            }
        }

        binding.searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchInput.text.isNullOrEmpty() &&
                (viewModel.getHistory().value?.isNotEmpty() == true)
            ) {
                binding.searchHistoryLayout.visibility = View.VISIBLE
            } else {
                binding.searchHistoryLayout.visibility = View.GONE
            }
        }

        binding.clearHistoryButton.setOnClickListener {
            historyRepository.clearHistory()
            binding.searchHistoryLayout.visibility = View.GONE
        }

        if (savedInstanceState != null) {
            val restoredText = savedInstanceState.getString(SEARCH_TEXT_KEY)
            binding.searchInput.setText(restoredText)
        }

        binding.backButton.setNavigationOnClickListener { finish() }
        binding.update.setOnClickListener {
            val query = binding.searchInput.text.toString()
            if (query.isNotEmpty()) {
                viewModel.searchTracks(query)
            }
        }
        binding.clearButton.setOnClickListener {
            binding.searchInput.setText("")
            hideKeyboard()
            binding.recyclerView.visibility = View.GONE
            binding.placeholderNothing.visibility = View.GONE
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
        }

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (!s.isNullOrEmpty()) {
                    binding.searchHistoryLayout.visibility = View.GONE
                    searchDebounce()
                } else {
                    if (viewModel.getHistory().value?.isNotEmpty() == true) {
                        binding.searchHistoryLayout.visibility = View.VISIBLE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.searchInput.text.toString()
                if (query.isNotEmpty()) {
                    viewModel.searchTracks(query)
                }
                true
            } else false
        }
    }

    private fun goToAudioPlayer(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra(TRACK, Gson().toJson(track))
        startActivity(intent)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, binding.searchInput.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(SEARCH_TEXT_KEY)
        binding.searchInput.setText(restoredText)
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "TEXT_KEY"
        private const val TRACK = "TRACK_DATA"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}