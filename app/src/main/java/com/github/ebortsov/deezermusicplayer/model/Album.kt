package com.github.ebortsov.deezermusicplayer.model

import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
data class Album(
    val id: Long,
    val title: String,
    @Serializable(with = JavaURISerializer::class)
    val coverUri: URI,
)