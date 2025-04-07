package com.github.ebortsov.deezermusicplayer.network.deezer.models

import kotlinx.serialization.Serializable

@Serializable
data class SearchResultApiModel(
    val data: List<TrackApiModel>,
    val total: Int,
    val prev: String? = null,
    val next: String? = null
)
