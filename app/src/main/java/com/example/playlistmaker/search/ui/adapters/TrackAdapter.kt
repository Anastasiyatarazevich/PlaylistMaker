package com.example.playlistmaker.search.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.models.Track

class TrackAdapter(private val trackList: List<Track>) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    var onClickTrack: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onClickTrack?.invoke(track)
        }
    }

    override fun getItemCount(): Int = trackList.size

    class TrackViewHolder(private val binding: TrackItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(track: Track) {
            binding.trackName.text = track.trackName
            binding.artistName.text = track.artistName
            binding.trackTime.text = android.text.format.DateFormat.format("mm:ss", track.trackTimeMillis)
            Glide.with(binding.root.context)
                .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
                .placeholder(com.example.playlistmaker.R.drawable.placeholder)
                .into(binding.artworkImage)
        }
    }
}