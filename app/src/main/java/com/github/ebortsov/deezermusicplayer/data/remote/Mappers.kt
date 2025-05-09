package com.github.ebortsov.deezermusicplayer.data.remote

import com.github.ebortsov.deezermusicplayer.network.deezer.models.AlbumApiModel
import com.github.ebortsov.deezermusicplayer.network.deezer.models.ArtistApiModel
import com.github.ebortsov.deezermusicplayer.network.deezer.models.TrackApiModel
import com.github.ebortsov.deezermusicplayer.model.Album
import com.github.ebortsov.deezermusicplayer.model.Artist
import com.github.ebortsov.deezermusicplayer.model.Track
import java.net.URI

fun ArtistApiModel.toArtist(): Artist {
    return Artist(
        id = id,
        name = name,
    )
}

fun AlbumApiModel.toAlbum(): Album {
    return Album(
        id = id,
        title = title,
        coverUri = URI.create(cover_xl),
    )
}

fun TrackApiModel.toTrack(): Track {
    return Track(
        id = id,
        title = title,
        previewUri = URI.create(preview),
        artist = artist.toArtist(),
        album = album.toAlbum()
    )
}