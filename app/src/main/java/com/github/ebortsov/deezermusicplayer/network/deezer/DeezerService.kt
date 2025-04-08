package com.github.ebortsov.deezermusicplayer.network.deezer

import com.github.ebortsov.deezermusicplayer.network.deezer.models.SearchResultApiModel
import com.github.ebortsov.deezermusicplayer.network.deezer.models.TrackApiModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DeezerService {
    @GET("search")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("index") index: Int = 0,
    ): SearchResultApiModel

    @GET("track/{id}")
    suspend fun getTrack(@Path("id") trackId: Long): TrackApiModel
}