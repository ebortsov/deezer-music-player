package com.github.ebortsov.deezermusicplayer.data.deezerapi

import com.github.ebortsov.deezermusicplayer.model.Track

interface ApiDataSource {
    suspend fun searchTracks(searchQuery: String): List<Track>

    suspend fun getTrack(trackId: Long): Track
}