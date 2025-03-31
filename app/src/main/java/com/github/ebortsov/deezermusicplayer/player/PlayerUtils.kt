package com.github.ebortsov.deezermusicplayer.player

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.github.ebortsov.deezermusicplayer.model.Track

fun createMediaItemFromTrack(track: Track): MediaItem {
    val metadata = MediaMetadata.Builder()
        .setTitle(track.title)
        .setArtist(track.artist.name)
        .setAlbumTitle(track.album.title)
        .setArtworkUri(Uri.parse(track.album.coverBigUri.toString()))
        .build()

    val mediaItem = MediaItem
        .Builder()
        .setUri(track.previewLink.toString())
        .setMediaMetadata(metadata)
        .build()

    return mediaItem
}