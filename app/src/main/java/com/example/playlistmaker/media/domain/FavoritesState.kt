package com.example.playlistmaker.media.domain

sealed interface FavoritesState {
    object Empty : FavoritesState
}