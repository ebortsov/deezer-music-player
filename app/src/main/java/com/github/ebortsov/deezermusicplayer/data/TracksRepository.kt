package com.github.ebortsov.deezermusicplayer.data

import com.github.ebortsov.deezermusicplayer.data.deezerapi.ApiDataSource
import com.github.ebortsov.deezermusicplayer.data.deezerapi.DeezerApiDataSource
import com.github.ebortsov.deezermusicplayer.model.Track

class TracksRepository(
    private val apiDataSource: ApiDataSource = DeezerApiDataSource.createUsingRetrofit()
) {
    suspend fun searchTrackInNetwork(searchQuery: String): List<Track> {
        return apiDataSource.searchTracks(searchQuery)
    }
}