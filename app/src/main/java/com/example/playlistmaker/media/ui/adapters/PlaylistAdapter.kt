package com.example.playlistmaker.media.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.models.Playlist

class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    private val playlists = mutableListOf<Playlist>()

    fun setItems(newItems: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tabs_playlist_layout, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image = itemView.findViewById<ImageView>(R.id.playlist_image)
        private val name = itemView.findViewById<TextView>(R.id.playlist_name)
        private val tracks = itemView.findViewById<TextView>(R.id.playlist_count)

        fun bind(playlist: Playlist) {
            name.text = playlist.name
            tracks.text = "${playlist.trackCount} трек${getTrackSuffix(playlist.trackCount)}"

            val context = itemView.context
            val placeholder = R.drawable.placeholder

            Glide.with(context)
                .load(playlist.imagePath ?: "")
                .placeholder(placeholder)
                .error(placeholder)
                .centerCrop()
                .into(image)
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