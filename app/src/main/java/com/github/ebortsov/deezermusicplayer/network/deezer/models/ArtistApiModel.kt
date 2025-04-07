package com.github.ebortsov.deezermusicplayer.network.deezer.models

import kotlinx.serialization.Serializable

@Suppress("PropertyName")
@Serializable
data class ArtistApiModel(
    val id: Long,
    val name: String,
    val link: String,
    val picture: String,
    val picture_small: String,
    val picture_medium: String,
    val picture_big: String,
    val picture_xl: String,
    val tracklist: String,
    val type: String
)