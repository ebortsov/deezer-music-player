package com.github.ebortsov.deezermusicplayer.utils

fun formatMilliseconds(ms: Long): String {
    val wholeMinutes = ms / (60 * 1000)
    val wholeSeconds = ms % (60 * 1000) / 1000
    val minutesFormatted = wholeMinutes.toString().padStart(2, '0')
    val secondsFormatted = wholeSeconds.toString().padStart(2, '0')
    return "$minutesFormatted:$secondsFormatted"
}