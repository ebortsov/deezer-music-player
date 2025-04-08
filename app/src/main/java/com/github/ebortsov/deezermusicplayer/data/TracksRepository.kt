package com.github.ebortsov.deezermusicplayer.data

import com.github.ebortsov.deezermusicplayer.data.local.TracksLocalDataSource
import com.github.ebortsov.deezermusicplayer.data.remote.TracksRemoteDataSource
import com.github.ebortsov.deezermusicplayer.model.Track

class TracksRepository(
    private val tracksLocalDataSource: TracksLocalDataSource,
    private val tracksRemoteDataSource: TracksRemoteDataSource
) {
    suspend fun searchTrackInNetwork(searchQuery: String): List<Track> {
        return tracksRemoteDataSource.searchTracks(searchQuery)
    }

    suspend fun downloadTrack(track: Track): Boolean {
        return tracksLocalDataSource.downloadTrack(track)
    }

    suspend fun removeLocalTrack(track: Track) {
        tracksLocalDataSource.removeTrack(track)
    }

    suspend fun getLocalTracks(): List<Track> {
        return tracksLocalDataSource.getTracks()
    }

    suspend fun searchTrackInLocal(searchQuery: String): List<Track> {
        val savedTracks = getLocalTracks()
        // TODO: implement some searching logic here
        return savedTracks
    }
}