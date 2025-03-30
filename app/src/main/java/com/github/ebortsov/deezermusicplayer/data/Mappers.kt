package com.github.ebortsov.deezermusicplayer.data

import com.github.ebortsov.deezermusicplayer.data.deezerapi.apimodels.AlbumApiModel
import com.github.ebortsov.deezermusicplayer.data.deezerapi.apimodels.ArtistApiModel
import com.github.ebortsov.deezermusicplayer.data.deezerapi.apimodels.TrackApiModel
import com.github.ebortsov.deezermusicplayer.model.Album
import com.github.ebortsov.deezermusicplayer.model.Artist
import com.github.ebortsov.deezermusicplayer.model.Track
import java.net.URI

fun ArtistApiModel.toArtist(): Artist {
    return Artist(
        id = id,
        name = name,
        pictureUri = URI.create(picture),
        pictureSmallUri = URI.create(picture_small),
        pictureMediumUri = URI.create(picture_medium),
        pictureBigUri = URI.create(picture_big)
    )
}

fun AlbumApiModel.toAlbum(): Album {
    return Album(
        id = id,
        title = title,
        coverSmallUri = URI.create(cover_small),
        coverMediumUri = URI.create(cover_medium),
        coverBigUri = URI.create(cover_big)
    )
}

fun TrackApiModel.toTrack(): Track {
    return Track(
        id = id,
        title = title,
        previewLink = URI.create(preview),
        artist = artist.toArtist(),
        album = album.toAlbum()
    )
}