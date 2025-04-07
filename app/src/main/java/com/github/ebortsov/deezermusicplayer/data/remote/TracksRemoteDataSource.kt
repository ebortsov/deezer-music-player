package com.github.ebortsov.deezermusicplayer.data.remote

import com.github.ebortsov.deezermusicplayer.model.Track

interface TracksRemoteDataSource {
    suspend fun searchTracks(searchQuery: String): List<Track>
}