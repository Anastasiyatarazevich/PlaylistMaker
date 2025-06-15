package com.example.playlistmaker.db

import com.example.playlistmaker.models.Track

class TrackDbConverter {
    fun map(entity: FavoriteTrackEntity): Track {
        return Track(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTimeMillis = entity.trackTimeMillis,
            artworkUrl100 = entity.artworkUrl100,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            genreName = entity.genreName,
            country = entity.country,
            previewUrl = entity.previewUrl
        )
    }

    fun map(track: Track): FavoriteTrackEntity {
        return FavoriteTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            genreName = track.genreName,
            country = track.country,
            previewUrl = track.previewUrl,
            addedTimestamp = System.currentTimeMillis()
        )
    }
}