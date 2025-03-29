package com.github.ebortsov.deezermusicplayer.data

import com.github.ebortsov.deezermusicplayer.data.deezerapi.apimodels.SearchResultItemApiModel
import com.github.ebortsov.deezermusicplayer.data.deezerapi.apimodels.TrackApiModel
import com.github.ebortsov.deezermusicplayer.model.Track
import java.net.URI

fun SearchResultItemApiModel.toTrack(): Track {
    return Track(
        id = id.toString(),
        title = title,
        previewLink = URI.create(preview)
    )
}

fun TrackApiModel.toTrack(): Track {
    return Track(
        id = id.toString(),
        title = title,
        previewLink = URI.create(preview)
    )
}

