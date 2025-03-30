package com.github.ebortsov.deezermusicplayer

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ebortsov.deezermusicplayer.model.Album
import com.github.ebortsov.deezermusicplayer.model.Artist
import com.github.ebortsov.deezermusicplayer.model.Track
import com.github.ebortsov.deezermusicplayer.player.createMediaItemFromTrack

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.net.URI

@RunWith(AndroidJUnit4::class)
class PlayerUtilTest {
    @Test
    fun track_is_correctly_converted_to_MediaItem() {
        val stubUri = URI.create("example.com")
        val track = Track(
            2394714525,
            "Scream",
            URI.create("example.com"),
            artist = Artist(
                id = 15219,
                name = "Dreamcatcher",
                pictureUri = stubUri,
                pictureSmallUri = stubUri,
                pictureMediumUri = stubUri,
                pictureBigUri = stubUri
            ),
            album = Album(
                id = 471967745,
                title = "1st Album [Dystopia : The Tree of Language]",
                coverSmallUri = stubUri,
                coverMediumUri = stubUri,
                coverBigUri = stubUri
            )
        )
        val mediaItem = createMediaItemFromTrack(track)

        val metadata = mediaItem.mediaMetadata

        // Verify the correctness of metadata
        assertEquals(track.title, metadata.title)
        assertEquals(track.album.title, metadata.albumTitle)
        assertEquals(track.artist.name, metadata.artist)
        assertEquals(track.album.coverBigUri.toString(), metadata.artworkUri.toString())
    }
}