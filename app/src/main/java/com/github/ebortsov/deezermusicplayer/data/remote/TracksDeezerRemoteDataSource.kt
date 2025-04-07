package com.github.ebortsov.deezermusicplayer.data.remote

import com.github.ebortsov.deezermusicplayer.model.Track

class TracksDeezerRemoteDataSource(

) : TracksRemoteDataSource {
    override suspend fun searchTracks(searchQuery: String): List<Track> {
        TODO("Not yet implemented")
    }
}