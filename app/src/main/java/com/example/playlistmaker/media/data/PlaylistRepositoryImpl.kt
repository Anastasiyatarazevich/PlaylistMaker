package com.example.playlistmaker.media.data

import com.example.playlistmaker.db.PlaylistDao
import com.example.playlistmaker.db.PlaylistEntity
import com.example.playlistmaker.db.PlaylistTrackEntity
import com.example.playlistmaker.db.PlaylistTracksDao
import com.example.playlistmaker.media.domain.PlaylistRepository
import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val dao: PlaylistDao,
    private val playlistTrackDao: PlaylistTracksDao,
    private val gson: Gson
) : PlaylistRepository {

    override suspend fun createPlaylist(name: String, description: String?, imagePath: String?) {
        val playlist = PlaylistEntity(
            name = name,
            description = description,
            imagePath = imagePath,
            trackIdsJson = gson.toJson(emptyList<Int>()),
            trackCount = 0
        )
        dao.insertPlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return dao.getAllPlaylists().map { entityList ->
            entityList.map { entity ->
                Playlist(
                    id = entity.playlistId,
                    name = entity.name,
                    description = entity.description,
                    imagePath = entity.imagePath,
                    trackIds = gson.fromJson(entity.trackIdsJson, Array<Int>::class.java).toList(),
                    trackCount = entity.trackCount
                )
            }
        }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val updatedTrackIds = playlist.trackIds.toMutableList()
        updatedTrackIds.add(track.trackId)

        val updatedEntity = PlaylistEntity(
            playlistId = playlist.id,
            name = playlist.name,
            description = playlist.description,
            imagePath = playlist.imagePath,
            trackIdsJson = gson.toJson(updatedTrackIds),
            trackCount = playlist.trackCount + 1
        )

        dao.updatePlaylist(updatedEntity)

        val trackEntity = PlaylistTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            genreName = track.genreName,
            country = track.country,
        )

        playlistTrackDao.insertTrack(trackEntity)
    }

    override fun isTrackInPlaylist(trackId: Int, playlist: Playlist): Boolean {
        return playlist.trackIds.contains(trackId)
    }
}