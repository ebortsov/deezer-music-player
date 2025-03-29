package com.github.ebortsov.deezermusicplayer.data.deezerapi.apimodels

import kotlinx.serialization.Serializable

@Serializable
data class TrackApiModel(
    val id: Long,
    val readable: Boolean,
    val title: String,
    val title_short: String,
    val title_version: String,
    val link: String,
    val duration: Long,
    val track_position: Long,
    val disk_number: Long,
    val rank: Long,
    val release_date: String,
    val explicit_lyrics: Boolean,
    val explicit_content_lyrics: Int,
    val explicit_content_cover: Int,
    val preview: String
)