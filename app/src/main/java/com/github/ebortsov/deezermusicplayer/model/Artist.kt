package com.github.ebortsov.deezermusicplayer.model

import java.net.URI

data class Artist(
    val id: Long,
    val name: String,
    val pictureUri: URI,
    val pictureSmallUri: URI,
    val pictureMediumUri: URI,
    val pictureBigUri: URI
)