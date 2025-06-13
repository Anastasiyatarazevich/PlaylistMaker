package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.player.ui.AudioPlayerActivity
import com.example.playlistmaker.search.domain.SearchState
import com.example.playlistmaker.search.ui.adapters.TrackAdapter
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding

    private val viewModel: SearchViewModel by viewModel { parametersOf(requireContext()) }

    private var trackList = ArrayList<Track>()
    private var trackHistoryList = ArrayList<Track>()

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter

    private var isClickAllowed = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter(trackList)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = trackAdapter

        trackHistoryAdapter = TrackAdapter(trackHistoryList)
        binding.searchHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchHistoryRecyclerView.adapter = trackHistoryAdapter

        lifecycleScope.launchWhenStarted {
            viewModel.screenState.collectLatest { state ->
                renderState(state)
            }
        }

        viewModel.getHistory().observe(viewLifecycleOwner) { history ->
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
            if (hasFocus && binding.searchInput.text.isNullOrEmpty()
                && (viewModel.getHistory().value?.isNotEmpty() == true)
            ) {
                binding.searchHistoryLayout.visibility = View.VISIBLE
            } else {
                binding.searchHistoryLayout.visibility = View.GONE
            }
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            binding.searchHistoryLayout.visibility = View.GONE
        }

        binding.update.setOnClickListener {
            val query = binding.searchInput.text.toString()
            if (query.isNotEmpty()) {
                viewModel.performSearchDebounced(query)
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
                binding.clearButton.visibility =
                    if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (!s.isNullOrEmpty()) {
                    binding.searchHistoryLayout.visibility = View.GONE
                    viewModel.performSearchDebounced(s.toString())
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
                    viewModel.performSearchDebounced(query)
                }
                true
            } else false
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
    }

    private fun goToAudioPlayer(track: Track) {
        val intent = Intent(requireContext(), AudioPlayerActivity::class.java)
        intent.putExtra(TRACK, track)
        startActivity(intent)
    }

    private fun renderState(state: SearchState) {
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

    companion object {
        private const val TRACK = "TRACK_DATA"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}