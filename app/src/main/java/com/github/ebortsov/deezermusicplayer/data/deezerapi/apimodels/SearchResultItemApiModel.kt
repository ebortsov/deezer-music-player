package com.github.ebortsov.deezermusicplayer.data.deezerapi.apimodels

import kotlinx.serialization.Serializable

@Serializable
data class SearchResultItemApiModel(
    val id: Long,
    val readable: Boolean,
    val title: String,
    val title_short: String,
    val title_version: String,
    val link: String,
    val duration: Int,
    val rank: Long,
    val explicit_lyrics: Boolean,
    val preview: String
)
