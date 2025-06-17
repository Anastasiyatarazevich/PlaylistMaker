package com.example.playlistmaker.media.domain

import com.example.playlistmaker.models.Playlist

sealed class PlaylistsState {
    object Empty : PlaylistsState()
    data class Content(val playlists: List<Playlist>) : PlaylistsState()
}