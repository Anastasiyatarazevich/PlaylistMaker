package com.example.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.media.domain.PlaylistsState
import com.example.playlistmaker.media.ui.adapters.PlaylistAdapter
import com.example.playlistmaker.media.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistFragment : Fragment() {
    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var adapter: PlaylistAdapter

    private lateinit var binding: FragmentPlaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PlaylistAdapter()

        binding.recyclerViewPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewPlaylists.adapter = adapter

        viewModel.getState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistsState.Empty -> {
                    binding.recyclerViewPlaylists.visibility = View.GONE
                    binding.placeholderNoplaylists.visibility = View.VISIBLE
                }

                is PlaylistsState.Content -> {
                    binding.recyclerViewPlaylists.visibility = View.VISIBLE
                    binding.placeholderNoplaylists.visibility = View.GONE
                    adapter.setItems(state.playlists)
                }
            }
        }
        binding.update.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_createPlaylistFragment)
        }
    }

    companion object {
        fun newInstance() = PlaylistFragment()
    }
}
