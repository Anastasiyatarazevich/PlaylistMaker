package com.example.playlistmaker.media.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.media.domain.ImageSaver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ImageSaverImpl(private val context: Context) : ImageSaver {

    override suspend fun saveImage(uri: Uri): String = withContext(Dispatchers.IO) {
        val picturesDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!picturesDir.exists()) picturesDir.mkdirs()

        val file = File(picturesDir, "playlist_cover_${System.currentTimeMillis()}.jpg")

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                BitmapFactory.decodeStream(input)
                    .compress(Bitmap.CompressFormat.JPEG, 90, output)
            }
        }

        return@withContext file.toURI().toString()
    }
}
