package com.github.ebortsov.deezermusicplayer.data.deezerapi.apimodels

import kotlinx.serialization.Serializable

@Suppress("PropertyName")
@Serializable
data class TrackApiModel(
    val id: Long,
    val readable: Boolean,
    val title: String,
    val title_short: String,
    val link: String, // link to the deezer
    val duration: Int,
    val rank: Int,
    val explicit_lyrics: Boolean,
    val explicit_content_lyrics: Int,
    val explicit_content_cover: Int,
    val preview: String, // actual preview link
    val md5_image: String,
    val artist: ArtistApiModel,
    val album: AlbumApiModel,
    val type: String
)