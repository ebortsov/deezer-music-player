package com.github.ebortsov.deezermusicplayer.data.remote

import com.github.ebortsov.deezermusicplayer.model.Track

/**
 * Interface that represents some remote source of tracks
 */
interface TracksRemoteDataSource {
    suspend fun searchTracks(searchQuery: String): List<Track>
}