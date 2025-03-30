package com.github.ebortsov.deezermusicplayer.data.deezerapi

import com.github.ebortsov.deezermusicplayer.data.deezerapi.apimodels.SearchResultApiModel
import com.github.ebortsov.deezermusicplayer.data.deezerapi.apimodels.TrackApiModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DeezerApi {
    @GET("search")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("index") index: Int = 0,
    ): SearchResultApiModel

    @GET("track/{id}")
    suspend fun getTrack(@Path("id") trackId: Long): TrackApiModel
}