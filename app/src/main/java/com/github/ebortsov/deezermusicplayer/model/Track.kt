package com.github.ebortsov.deezermusicplayer.model

import java.net.URI

data class Track(
    val id: Long,
    val title: String,
    val previewLink: URI, // let's believe the preview link does not expire...
    val artist: Artist,
    val album: Album
)
