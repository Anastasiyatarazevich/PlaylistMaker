package com.example.playlistmaker.media.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.example.playlistmaker.main.MainActivity
import com.example.playlistmaker.media.ui.viewmodel.PlaylistInfoViewModel
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.player.ui.AudioPlayerActivity
import com.example.playlistmaker.search.ui.adapters.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf


class PlaylistInfoFragment : Fragment() {
    private lateinit var viewModel: PlaylistInfoViewModel
    private lateinit var binding: FragmentPlaylistInfoBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var moreSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var playlistAdapter: TrackAdapter
    private val trackList = mutableListOf<Track>()
    private var isClickAllowed = true
    var currentPlaylistName = ""
    var currentDescription = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = arguments?.getInt("playlistId") ?: -1
        viewModel = getViewModel { parametersOf(playlistId) }

        (activity as? MainActivity)?.showBottomNav(false)

        playlistAdapter = TrackAdapter(trackList)

        playlistAdapter.onClickTrack = { track ->
            if (clickDebounce()) {
                goToAudioPlayer(track)
            }
        }

        playlistAdapter.onLongClickTrack = { track ->
            val dialog = AlertDialog.Builder(requireContext())
                .setMessage("Хотите удалить трек?")
                .setNegativeButton("НЕТ") { d, _ ->
                    d.dismiss()
                }
                .setPositiveButton("ДА") { d, _ ->
                    d.dismiss()
                    viewModel.removeTrackFromPlaylist(track)
                }
                .show()

            val positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            positive.setTextColor(ContextCompat.getColor(requireContext(), R.color.background_blue))
            negative.setTextColor(ContextCompat.getColor(requireContext(), R.color.background_blue))
        }

        binding.playlistsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistsRecycler.adapter = playlistAdapter

        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            currentPlaylistName = playlist.name
            currentDescription = playlist.description.orEmpty()

            binding.playlistName.text = playlist.name
            binding.description.text = playlist.description ?: ""

            val placeholder = R.drawable.placeholder
            Glide.with(binding.root)
                .load(playlist.imagePath ?: "")
                .placeholder(placeholder)
                .error(placeholder)
                .centerCrop()
                .into(binding.placeholderTrack)

            binding.playlistNameMore.text = currentPlaylistName
            binding.descriptionMore.text = currentDescription


            Glide.with(binding.root)
                .load(playlist.imagePath ?: "")
                .placeholder(placeholder)
                .error(placeholder)
                .centerCrop()
                .into(binding.artworkImage)
        }

        viewModel.tracksCount.observe(viewLifecycleOwner) { count ->
            binding.countOfTrack.text = "$count трек${getTrackSuffix(count)}"
        }

        binding.backButton.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        viewModel.durationMinutes.observe(viewLifecycleOwner) { minutes ->

            val minuteWord = when {
                minutes % 100 in 11..14 -> "минут"
                minutes % 10 == 1 -> "минута"
                minutes % 10 in 2..4 -> "минуты"
                else -> "минут"
            }

            binding.timeOfTrack.text = getString(R.string.minutes_format, minutes, minuteWord)
        }


        moreSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheetMore).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
        }

        binding.overlay.setOnClickListener {
            moreSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.overlay.visibility = View.GONE
        }

        binding.more.setOnClickListener {
            binding.overlay.visibility = View.VISIBLE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            moreSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            isHideable = false
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) binding.playlistsBottomSheet.isEnabled =
                        false
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }

        moreSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheetMore).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) binding.overlay.visibility =
                        View.GONE
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            binding.playlistsBottomSheet.visibility = View.VISIBLE
            Log.e("HUH", trackList.toString())
            trackList.clear()
            trackList.addAll(tracks)
            playlistAdapter.notifyDataSetChanged()
        }

        binding.buttonShare.setOnClickListener {
            share()
        }

        binding.buttonDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Удалить плейлист")
                .setMessage("Хотите удалить плейлист?")
                .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Да") { dialog, _ ->
                    dialog.dismiss()
                    viewModel.deleteCurrentPlaylist()
                }
                .show()
        }

        viewModel.playlistDeleted.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        binding.shareApp.setOnClickListener {
            share()
        }

    }

    private fun getTrackSuffix(count: Int): String {
        return when {
            count % 100 in 11..14 -> "ов"
            count % 10 == 1 -> ""
            count % 10 in 2..4 -> "а"
            else -> "ов"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.showBottomNav(true)
    }

    private fun goToAudioPlayer(track: Track) {
        val intent = Intent(requireContext(), AudioPlayerActivity::class.java)
        intent.putExtra(TRACK, track)
        startActivity(intent)
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

    private fun share() {
        if (trackList.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "В этом плейлисте нет списка треков, которым можно поделиться",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val sb = StringBuilder()
            sb.append(currentPlaylistName).append("\n")
            if (currentDescription.isNotBlank()) {
                sb.append(currentDescription).append("\n")
            }
            sb.append("${trackList.size} трек${getTrackSuffix(trackList.size)}\n\n")
            trackList.forEachIndexed { index, track ->
                val duration = DateFormat.format("mm:ss", track.trackTimeMillis)
                sb.append("${index + 1}. ${track.artistName} - ${track.trackName} ($duration)\n")
            }
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, sb.toString().trim())
            }
            startActivity(
                Intent.createChooser(shareIntent, "Поделиться плейлистом")
            )
        }
    }


    companion object {
        private const val TRACK = "TRACK_DATA"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
