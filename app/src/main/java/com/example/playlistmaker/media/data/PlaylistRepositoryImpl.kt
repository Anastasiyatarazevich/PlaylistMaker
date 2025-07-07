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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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

    override fun getTracksByPlaylist(playlistId: Int): Flow<List<Track>> = flow {
        val playlistEntity = dao.getPlaylistById(playlistId)
        if (playlistEntity != null) {
            val trackIds = gson.fromJson(playlistEntity.trackIdsJson, Array<Int>::class.java).toList()
            playlistTrackDao.getTracksByIdsFlow(trackIds)
                .map { entities ->
                    entities.map { entity ->
                        Track(
                            trackId = entity.trackId,
                            trackName = entity.trackName,
                            artistName = entity.artistName,
                            trackTimeMillis = entity.trackTimeMillis,
                            artworkUrl100 = entity.artworkUrl100,
                            collectionName = entity.collectionName,
                            releaseDate = entity.releaseDate,
                            genreName = entity.genreName,
                            country = entity.country,
                            previewUrl = ""
                        )
                    }
                }.collect { emit(it) }
        } else {
            emit(emptyList())
        }
    }
    override fun getPlaylistById(id: Int): Flow<Playlist> = flow {
        val entity = dao.getPlaylistById(id)
        if (entity != null) {
            emit(
                Playlist(
                    id = entity.playlistId,
                    name = entity.name,
                    description = entity.description,
                    imagePath = entity.imagePath,
                    trackIds = gson.fromJson(entity.trackIdsJson, Array<Int>::class.java).toList(),
                    trackCount = entity.trackCount
                )
            )
        }
    }

    override suspend fun removeTrackFromPlaylist(track: Track, playlistId: Int) {
        val entity = dao.getPlaylistById(playlistId) ?: return

        val ids = gson.fromJson(entity.trackIdsJson, Array<Int>::class.java).toMutableList()
        if (!ids.remove(track.trackId)) return

        val updated = entity.copy(
            trackIdsJson = gson.toJson(ids),
            trackCount = ids.size
        )
        dao.updatePlaylist(updated)

        val allPlaylists = dao.getAllPlaylistsOnce()
        val allIds = allPlaylists.flatMap {
            gson.fromJson(it.trackIdsJson, Array<Int>::class.java).toList()
        }
        if (track.trackId !in allIds) {
            playlistTrackDao.deleteByTrackId(track.trackId)
        }
    }

    override suspend fun deletePlaylist(id: Int) {
        dao.deletePlaylistById(id)

        val allJsons = dao.getAllTrackIdsJsonFlow().first()
        val allIds = allJsons
            .flatMap { json -> gson.fromJson(json, Array<Int>::class.java).toList() }
            .toSet()


        dao.getAllTrackIdsJsonFlow()
        allIdsFromTracksTable().forEach { trackId ->
            if (trackId !in allIds) {
                playlistTrackDao.deleteByTrackId(trackId)
            }
        }
    }

    private suspend fun allIdsFromTracksTable(): List<Int> =
        playlistTrackDao.getAllTrackIds()

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = PlaylistEntity(
            playlistId   = playlist.id,
            name         = playlist.name,
            description  = playlist.description,
            imagePath    = playlist.imagePath,
            trackIdsJson = gson.toJson(playlist.trackIds),
            trackCount   = playlist.trackCount
        )
        dao.updatePlaylist(entity)
    }
}