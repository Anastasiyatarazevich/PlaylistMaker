package com.example.playlistmaker.player.domain

interface AudioPlayerRepository {
    val playerState: Int
    fun startPlayer()
    fun pausePlayer()
    fun release()
    fun setProgressListener(listener: (String) -> Unit)
    fun setCompletionListener(listener: () -> Unit)
}