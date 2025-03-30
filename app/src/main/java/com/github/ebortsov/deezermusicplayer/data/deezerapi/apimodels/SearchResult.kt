package com.github.ebortsov.deezermusicplayer.data.deezerapi.apimodels

import kotlinx.serialization.Serializable

@Serializable
data class SearchResultApiModel(
    val data: List<TrackApiModel>,
    val total: Int,
    val prev: String? = null,
    val next: String? = null
)
