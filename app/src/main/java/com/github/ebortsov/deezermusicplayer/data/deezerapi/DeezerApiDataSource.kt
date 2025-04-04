package com.github.ebortsov.deezermusicplayer.data.deezerapi

import com.github.ebortsov.deezermusicplayer.model.Track
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.github.ebortsov.deezermusicplayer.data.toTrack
import okhttp3.MediaType.Companion.toMediaType

/*
* DataSource class that works with deezer api
* */

class DeezerApiDataSource(private val deezerApiService: DeezerApi) : ApiDataSource {
    override suspend fun searchTracks(searchQuery: String): List<Track> {
        val result = deezerApiService.searchTracks(searchQuery)

        return result.data.map { it.toTrack() }
    }

    override suspend fun getTrack(trackId: Long): Track {
        val result = deezerApiService.getTrack(trackId)

        return result.toTrack()
    }

    companion object {
        fun createUsingRetrofit(): DeezerApiDataSource {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.deezer.com/")
                .addConverterFactory(getSerializationConverterFactory())
                .build()

            val deezerApiService = retrofit.create(DeezerApi::class.java)
            return DeezerApiDataSource(deezerApiService)
        }
    }
}

// We need special converter factory in order to use Kotlin Serialization
private fun getSerializationConverterFactory(): Converter.Factory {
    val jsonStringFormat = Json { ignoreUnknownKeys = true }
    val contentType = "application/json"

    return jsonStringFormat.asConverterFactory(contentType.toMediaType())
}