package com.example.playlistmaker.media.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_bottom_sheet, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    inner class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name = view.findViewById<TextView>(R.id.playlist_name)
        private val image = view.findViewById<ImageView>(R.id.playlist_image)
        private val count = view.findViewById<TextView>(R.id.playlist_count)

        fun bind(playlist: Playlist) {
            name.text = playlist.name
            count.text = "${playlist.trackCount} трек${getTrackSuffix(playlist.trackCount)}"

            Glide.with(itemView)
                .load(playlist.imagePath)
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(ViewUtils.dpToPx(2f, itemView.context)))
                .into(image)

            itemView.setOnClickListener {
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
