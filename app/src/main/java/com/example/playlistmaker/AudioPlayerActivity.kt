package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.utils.ViewUtils
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var track: Track
    private lateinit var backButton: MaterialToolbar
    private lateinit var placeholderTrack: ImageView
    private lateinit var nameTrackData: TextView
    private lateinit var singerTrackData: TextView
    private lateinit var addTrackButton: ImageButton
    private lateinit var playTrackButton: ImageButton
    private lateinit var favoriteTrackButton: ImageButton
    private lateinit var timePlayTrack: TextView
    private lateinit var timeTrackLabel: TextView
    private lateinit var albumTrackLabel: TextView
    private lateinit var yearTrackLabel: TextView
    private lateinit var genreTrackLabel: TextView
    private lateinit var countryTrackLabel: TextView
    private lateinit var timeTrackData: TextView
    private lateinit var albumTrackData: TextView
    private lateinit var yearTrackData: TextView
    private lateinit var genreTrackData: TextView
    private lateinit var countryTrackData: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        initViews()

        val trackJson = intent.getStringExtra(TRACK)
        if (trackJson != null) {
            track = Gson().fromJson(trackJson, Track::class.java)
        } else {
            finish()
            return
        }
        setDataTrack()
        timePlayTrack.text = TIME_PLAY_TRACK
        backButton.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        backButton = findViewById(R.id.back_button)
        placeholderTrack = findViewById(R.id.placeholder_track)
        nameTrackData = findViewById(R.id.track_name)
        singerTrackData = findViewById(R.id.track_singer)
        addTrackButton = findViewById(R.id.add_track)
        playTrackButton = findViewById(R.id.play_track)
        favoriteTrackButton = findViewById(R.id.favorite_track)
        timePlayTrack = findViewById(R.id.time_track)
        timeTrackLabel = findViewById(R.id.time_of_track)
        albumTrackLabel = findViewById(R.id.album_track)
        yearTrackLabel = findViewById(R.id.year_track)
        genreTrackLabel = findViewById(R.id.genre_track)
        countryTrackLabel = findViewById(R.id.country_track)
        timeTrackData = findViewById(R.id.time_track_info)
        albumTrackData = findViewById(R.id.album_track_info)
        yearTrackData = findViewById(R.id.year_track_info)
        genreTrackData = findViewById(R.id.genre_track_info)
        countryTrackData = findViewById(R.id.country_track_info)
    }

    private fun setDataTrack() {
        nameTrackData.text = track.trackName
        singerTrackData.text = track.artistName
        timeTrackData.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        yearTrackData.text = track.releaseDate?.substringBefore("-")
        genreTrackData.text = track.genreName
        countryTrackData.text = track.country

        if (track.collectionName.isNullOrEmpty()) {
            albumTrackData.isVisible = false
            albumTrackLabel.isVisible = false
        } else {
            albumTrackData.isVisible = true
            albumTrackLabel.isVisible = true
            albumTrackData.text = track.collectionName
        }

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.place_holder_312)
            .transform(RoundedCorners(ViewUtils.dpToPx(8f, this)))
            .into(placeholderTrack)
    }

    companion object {
        private const val TRACK = "TRACK_DATA"
        private const val TIME_PLAY_TRACK = "0:30"
    }
}