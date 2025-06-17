package com.example.playlistmaker.media.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBottomSheetBinding
import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.search.ui.utils.ViewUtils

class PlaylistBottomSheetAdapter(
    private val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistBottomSheetAdapter.PlaylistViewHolder>() {

    private val playlists = mutableListOf<Playlist>()

    fun submitList(newList: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistBottomSheetBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PlaylistViewHolder(binding)
    }

    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    inner class PlaylistViewHolder(private val binding: ItemPlaylistBottomSheetBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.playlistName.text = playlist.name
            binding.playlistCount.text = "${playlist.trackCount} трек${getTrackSuffix(playlist.trackCount)}"

            Glide.with(binding.root)
                .load(playlist.imagePath)
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(ViewUtils.dpToPx(2f, binding.root.context)))
                .into(binding.playlistImage)

            binding.root.setOnClickListener {
                onClick(playlist)
            }
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
}
