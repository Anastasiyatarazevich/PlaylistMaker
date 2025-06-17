package com.example.playlistmaker.media.domain

import android.net.Uri

interface ImageSaver {
    suspend fun saveImage(uri: Uri): String
}