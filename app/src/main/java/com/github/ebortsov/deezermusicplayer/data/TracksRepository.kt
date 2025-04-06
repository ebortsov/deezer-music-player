package com.github.ebortsov.deezermusicplayer.data

import com.github.ebortsov.deezermusicplayer.data.deezerapi.ApiDataSource
import com.github.ebortsov.deezermusicplayer.data.deezerapi.DeezerApiDataSource
import com.github.ebortsov.deezermusicplayer.download.TrackLocalDataSource
import com.github.ebortsov.deezermusicplayer.model.Track
import kotlinx.coroutines.flow.Flow

class TracksRepository(
    private val apiDataSource: ApiDataSource = DeezerApiDataSource.createUsingRetrofit(),
    private val tracksLocalDataSource: TrackLocalDataSource = TrackLocalDataSource.getInstance()
) {
    suspend fun searchTrackInNetwork(searchQuery: String): List<Track> {
        return apiDataSource.searchTracks(searchQuery)
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

    fun getLocalTracksFlow(): Flow<List<Track>> {
        return tracksLocalDataSource.getTracksAsFlow()
    }

    suspend fun searchTrackInLocal(searchQuery: String): List<Track> {
        val savedTracks = getLocalTracks()
        // TODO: implement some searching logic here
        return savedTracks
    }
}