package com.example.playlistmaker

import android.annotation.SuppressLint
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
    private lateinit var nothingPlaceHolder: TextView
    private lateinit var noConnectionPlaceholder: LinearLayout
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var buttonUpdate: Button
    private lateinit var recyclerView: RecyclerView

    private val iTunesService = retrofit.create(ITunesApiService::class.java)

    private var searchText: String? = null

    companion object {
        private const val SEARCH_TEXT_KEY = "search_text_key"
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

        trackAdapter = TrackAdapter(trackList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

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
