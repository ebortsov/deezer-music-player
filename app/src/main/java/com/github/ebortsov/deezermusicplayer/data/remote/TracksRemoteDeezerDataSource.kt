package com.github.ebortsov.deezermusicplayer.data.remote

import com.github.ebortsov.deezermusicplayer.model.Track
import com.github.ebortsov.deezermusicplayer.network.deezer.DeezerService

class TracksRemoteDeezerDataSource(
    private val deezerService: DeezerService
) : TracksRemoteDataSource {
    override suspend fun searchTracks(searchQuery: String): List<Track> {
        val result = deezerService.searchTracks(searchQuery)

        return result.data.map { it.toTrack() }
    }
}