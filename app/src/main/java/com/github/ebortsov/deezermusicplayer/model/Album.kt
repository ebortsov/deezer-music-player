package com.github.ebortsov.deezermusicplayer.model

import java.net.URI

data class Album(
    val id: Long,
    val title: String,
    val coverSmallUri: URI,
    val coverMediumUri: URI,
    val coverBigUri: URI,
)