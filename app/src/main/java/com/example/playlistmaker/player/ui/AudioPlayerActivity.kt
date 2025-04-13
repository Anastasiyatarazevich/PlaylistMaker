package com.example.playlistmaker.player.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.domain.AudioPlayerState
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import com.example.playlistmaker.search.ui.utils.ViewUtils
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var viewModel: AudioPlayerViewModel
    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        extractTrackFromIntent()
        setupTrackInfo()

        initViewModel()
        setupObservers()
        setupPlaybackUI()
    }

    private fun extractTrackFromIntent() {
        val trackJson = intent.getStringExtra(TRACK)
        if (trackJson != null) {
            track = Gson().fromJson(trackJson, Track::class.java)
        } else {
            finish()
        }
    }

    private fun setupTrackInfo() {
        binding.trackName.text = track.trackName
        binding.trackSinger.text = track.artistName
        binding.timeTrackInfo.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(track.trackTimeMillis)
        binding.yearTrackInfo.text = track.releaseDate?.substringBefore("-") ?: RES_NO
        binding.genreTrackInfo.text = track.genreName ?: RES_NO
        binding.countryTrackInfo.text = track.country

        if (track.collectionName.isNullOrEmpty()) {
            binding.albumTrack.isVisible = false
            binding.albumTrackInfo.isVisible = false
        } else {
            binding.albumTrack.isVisible = true
            binding.albumTrackInfo.isVisible = true
            binding.albumTrackInfo.text = track.collectionName
        }
        val imageUrl = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.place_holder_312)
            .transform(RoundedCorners(ViewUtils.dpToPx(8f, this)))
            .into(binding.placeholderTrack)
    }

    private fun initViewModel() {
        val interactor = Creator.provideAudioPlayerInteractor()
        viewModel = ViewModelProvider(
            this,
            AudioPlayerViewModel.getViewModelFactory(track, interactor)
        ).get(AudioPlayerViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.getThemeSettings().observe(this) { state ->
            when (state) {
                is AudioPlayerState.Loading -> {
                    binding.timeTrack.text = TIME_PLAY_TRACK
                    binding.playTrack.setImageResource(R.drawable.play)
                }

                is AudioPlayerState.Content -> {
                    binding.timeTrack.text = state.currentTime
                    binding.playTrack.setImageResource(
                        if (state.isPlaying) R.drawable.pause else R.drawable.play
                    )
                }

                is AudioPlayerState.Error -> {
                    binding.timeTrack.text = TIME_PLAY_TRACK
                    binding.playTrack.setImageResource(R.drawable.play)
                }
            }
        }
    }

    private fun setupPlaybackUI() {
        binding.playTrack.setOnClickListener {
            viewModel.togglePlayback()
        }
        binding.backButton.setNavigationOnClickListener { finish() }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayback()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    companion object {
        const val TRACK = "TRACK_DATA"
        private const val TIME_PLAY_TRACK = "0:30"
        private const val RES_NO = "N/A"
    }
}