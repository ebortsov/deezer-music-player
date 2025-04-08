package com.github.ebortsov.deezermusicplayer.network.deezer

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val BASE_URL = "https://api.deezer.com/"

fun createDeezerApiService(): DeezerService {
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(getSerializationConverterFactory())
        .build()

    return retrofit.create(DeezerService::class.java)
}

fun getSerializationConverterFactory(): Converter.Factory {
    val jsonStringFormat = Json { ignoreUnknownKeys = true }
    val contentType = "application/json"

    return jsonStringFormat.asConverterFactory(contentType.toMediaType())
}