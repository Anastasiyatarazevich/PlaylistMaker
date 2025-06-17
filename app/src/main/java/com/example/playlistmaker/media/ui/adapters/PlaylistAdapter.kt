package com.example.playlistmaker.media.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TabsPlaylistLayoutBinding
import com.example.playlistmaker.models.Playlist

class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    private val playlists = mutableListOf<Playlist>()

    fun setItems(newItems: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = TabsPlaylistLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PlaylistViewHolder(binding)
    }

    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    class PlaylistViewHolder(private val binding: TabsPlaylistLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.playlistName.text = playlist.name
            binding.playlistCount.text = "${playlist.trackCount} трек${getTrackSuffix(playlist.trackCount)}"

            val context = binding.root.context
            val placeholder = R.drawable.placeholder

            Glide.with(context)
                .load(playlist.imagePath ?: "")
                .placeholder(placeholder)
                .error(placeholder)
                .centerCrop()
                .into(binding.playlistImage)
        }

        private fun getTrackSuffix(count: Int): String {
            return when {
                count % 100 in 11..14 -> "ов"
                count % 10 == 1 -> ""
                count % 10 in 2..4 -> "а"
                else -> "ов"
            }
        }
    }
}