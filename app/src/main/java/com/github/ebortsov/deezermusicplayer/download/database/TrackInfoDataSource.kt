package com.github.ebortsov.deezermusicplayer.download.database

import com.github.ebortsov.deezermusicplayer.model.Track
import kotlinx.coroutines.flow.Flow

class TrackInfoDataSource(
    private val trackInfoDatabase: TrackInfoDatabase
) {
    private val trackInfoDao = trackInfoDatabase.trackInfoDao()

    fun getTracksAsFlow(): Flow<List<Track>> = trackInfoDao.getTracksAsFlow()

    suspend fun getTracks(): List<Track> = trackInfoDao.getTracks()

    suspend fun removeTrack(track: Track) {
        trackInfoDao.removeTrack(TrackInfo(track.id, track))
    }

    suspend fun insertTrack(track: Track) {
        trackInfoDao.insertTrack(TrackInfo(track.id, track))
    }

    suspend fun clearTracks() {
        trackInfoDao.clearTable()
    }

    suspend fun isPresent(track: Track): Boolean {
        return trackInfoDao.findTrack(track.id).isNotEmpty()
    }
}
