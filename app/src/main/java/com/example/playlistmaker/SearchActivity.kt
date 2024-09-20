package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    private var searchText: String? = null

    companion object {
        private const val SEARCH_TEXT_KEY = "search_text_key"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val back = findViewById<ImageView>(R.id.back_button)
        val searchInput = findViewById<EditText>(R.id.search_input)
        val clearButton = findViewById<ImageView>(R.id.clear_button)


        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY)
            searchInput.setText(searchText)
        }

        back.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            searchInput.setText("")
            hideKeyboard()
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
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
}
