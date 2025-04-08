package com.github.ebortsov.deezermusicplayer.model

import com.github.ebortsov.deezermusicplayer.utils.JavaURISerializer
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
data class Track(
    val id: Long,
    val title: String,
    @Serializable(with = JavaURISerializer::class)
    val previewUri: URI,
    val artist: Artist,
    val album: Album
)
