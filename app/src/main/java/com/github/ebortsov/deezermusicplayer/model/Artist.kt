package com.github.ebortsov.deezermusicplayer.model

import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    val id: Long,
    val name: String,
)