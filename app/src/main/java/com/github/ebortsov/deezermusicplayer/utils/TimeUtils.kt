package com.github.ebortsov.deezermusicplayer.utils

import android.net.Uri
import java.net.URI

fun formatMilliseconds(ms: Long): String {
    val wholeMinutes = ms / (60 * 1000)
    val wholeSeconds = ms % (60 * 1000) / 1000
    val minutesFormatted = wholeMinutes.toString().padStart(2, '0')
    val secondsFormatted = wholeSeconds.toString().padStart(2, '0')
    return "$minutesFormatted:$secondsFormatted"
}

fun Uri.toJavaURI(): URI {
    return URI.create(toString())
}

fun URI.toAndroidUri(): Uri {
    return Uri.parse(toString())
}