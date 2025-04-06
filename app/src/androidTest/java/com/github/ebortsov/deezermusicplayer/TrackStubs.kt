package com.github.ebortsov.deezermusicplayer

import com.github.ebortsov.deezermusicplayer.model.Album
import com.github.ebortsov.deezermusicplayer.model.Artist
import com.github.ebortsov.deezermusicplayer.model.Track
import java.net.URI

fun createStubTracks(): List<Track> {
    return listOf(
        Track(
            2394714525,
            "Scream",
            URI.create("https://file-examples.com/storage/fe2465184067ef97996fb41/2017/11/file_example_MP3_5MG.mp3"),
            artist = Artist(
                id = 15219,
                name = "Dreamcatcher",
            ),
            album = Album(
                id = 471967745,
                title = "1st Album [Dystopia : The Tree of Language]",
                coverUri = URI.create("https://cdn-images.dzcdn.net/images/cover/a8a2163d4dd755f021955955f39a3ad9/1000x1000-000000-80-0-0.jpg"),
            )
        ),
        Track(
            2394714526,
            "BOCA",
            URI.create("https://file-examples.com/storage/fe2465184067ef97996fb41/2017/11/file_example_MP3_1MG.mp3"),
            artist = Artist(
                id = 15219,
                name = "Dreamcatcher",
            ),
            album = Album(
                id = 471967746,
                title = "Dystopia: Lose Myself",
                coverUri = URI.create("https://file-examples.com/storage/fe2465184067ef97996fb41/2017/10/file_example_PNG_2100kB.png"),
            )
        ),
        Track(
            2394714527,
            "Deja Vu",
            URI.create("https://file-examples.com/storage/fe2465184067ef97996fb41/2017/11/file_example_MP3_5MG.mp3"),
            artist = Artist(
                id = 15219,
                name = "Dreamcatcher",
            ),
            album = Album(
                id = 471967747,
                title = "Raid of Dream",
                coverUri = URI.create("https://file-examples.com/storage/fe2465184067ef97996fb41/2017/10/file_example_PNG_3MB.png"),
            )
        ),
    )
}

val invalidTrack = Track(
    2394714527,
    "Deja Vu",
    URI.create("https://this-url-does-not-exist-abacaba1337.mp3"),
    artist = Artist(
        id = 15219,
        name = "Dreamcatcher",
    ),
    album = Album(
        id = 471967747,
        title = "Raid of Dream",
        coverUri = URI.create("https://file-examples.com/storage/fe2465184067ef97996fb41/2017/10/file_example_PNG_3MB.png"),
    )
)