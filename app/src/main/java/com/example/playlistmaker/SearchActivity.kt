package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var trackList = ArrayList<Track>()
    private var trackListSearchHistory = ArrayList<Track>()
    private lateinit var nothingPlaceHolder: LinearLayout
    private lateinit var noConnectionPlaceholder: LinearLayout
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter
    private lateinit var buttonUpdate: Button
    private lateinit var recyclerView: RecyclerView

    private lateinit var searchHistoryLayout: ScrollView
    private lateinit var searchHistoryText: TextView
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var searchHistoryClearButton: Button
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var searchHistory: SearchHistory
    private lateinit var listenerSharedPrefs: SharedPreferences.OnSharedPreferenceChangeListener

    private val iTunesService = retrofit.create(ITunesApiService::class.java)

    private var searchText: String? = null

    companion object {
        private const val SEARCH_TEXT_KEY = "text_key"
        private const val SEARCH_HISTORY_SHARED_PREFS = "history_shared_prefs"
        private const val SEARCH_HISTORY_KEY = "history_key"
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val back = findViewById<MaterialToolbar>(R.id.back_button)
        val searchInput = findViewById<EditText>(R.id.search_input)
        val clearButton = findViewById<ImageView>(R.id.clear_button)


        nothingPlaceHolder = findViewById(R.id.placeholder_nothing)
        noConnectionPlaceholder = findViewById(R.id.placeholder_no_connection)
        recyclerView = findViewById(R.id.recycler_view)
        searchHistoryLayout = findViewById(R.id.search_history_layout)
        searchHistoryText = findViewById(R.id.search_history_text)
        searchHistoryRecyclerView = findViewById(R.id.search_history_recycler_view)
        searchHistoryClearButton = findViewById(R.id.clear_history_button)
        sharedPrefs = getSharedPreferences(SEARCH_HISTORY_SHARED_PREFS, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)

        trackAdapter = TrackAdapter(trackList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        trackHistoryAdapter = TrackAdapter(trackListSearchHistory)
        searchHistoryRecyclerView.adapter = trackHistoryAdapter
        searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this)


        trackAdapter.onClickTrack = { track: Track ->
            searchHistory.addTrack(track)
            updateSearchHistory()
        }

        trackHistoryAdapter.onClickTrack = { track: Track ->
            searchHistory.addTrack(track)
            updateSearchHistory()
        }


        listenerSharedPrefs =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPrefs, key ->
                if (key == SEARCH_HISTORY_KEY) {
                    trackListSearchHistory.clear()
                    trackListSearchHistory.addAll(searchHistory.getHistory())
                    trackHistoryAdapter.notifyDataSetChanged()
                }
            }

        sharedPrefs.registerOnSharedPreferenceChangeListener(listenerSharedPrefs)


        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchInput.text.toString()
                if (query.isNotEmpty()) {
                    searchTracks(query)
                }
            }
            false
        }

        searchInput.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && searchInput.text.isNullOrEmpty()
                && searchHistory.getHistory().isNotEmpty()
            ) {
                searchHistoryLayout.visibility = View.VISIBLE
                trackListSearchHistory.addAll(searchHistory.getHistory())
                trackHistoryAdapter.notifyDataSetChanged()
            } else {
                searchHistoryLayout.visibility = View.GONE
            }
        }

        searchHistoryClearButton.setOnClickListener {
            searchHistory.clearHistory()
            updateSearchHistory()
            searchHistoryLayout.visibility = View.GONE
        }



        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY)
            searchInput.setText(searchText)
        }

        back.setNavigationOnClickListener {
            finish()
        }

        buttonUpdate = findViewById(R.id.update)
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
                clearButton.visibility = clearButtonVisibility(s)


                if (searchInput.hasFocus() && s?.isNullOrEmpty() == true
                    && searchHistory.getHistory().isNotEmpty()
                ) {
                    searchHistoryLayout.visibility = View.VISIBLE
                    updateSearchHistory()
                } else {
                    searchHistoryLayout.visibility = View.GONE
                    trackList.clear()
                    trackAdapter.notifyDataSetChanged()
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
            }
            false

        }
    }

    fun updateSearchHistory() {
        trackListSearchHistory.clear()
        trackListSearchHistory.addAll(searchHistory.getHistory())
        trackHistoryAdapter.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val searchText = savedInstanceState.getString(SEARCH_TEXT_KEY)
        val searchInput: EditText? = findViewById(R.id.search_input)
        searchInput?.setText(searchText)
    }

    fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val searchInput: EditText? = findViewById(R.id.search_input)
        if (searchInput != null) {
            imm.hideSoftInputFromWindow(searchInput.windowToken, 0)
        }
    }

    fun searchTracks(term: String) {
        iTunesService.searchTracks(term)
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    val tracksResponse = response.body()
                    if (tracksResponse != null && tracksResponse.results.isNotEmpty()) {
                        trackList.clear()
                        trackList.addAll(tracksResponse.results)
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

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    trackList.clear()
                    trackAdapter.notifyDataSetChanged()
                    recyclerView.visibility = View.GONE
                    nothingPlaceHolder.visibility = View.GONE
                    noConnectionPlaceholder.visibility = View.VISIBLE
                }
            })
    }
}
