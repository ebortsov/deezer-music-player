package com.github.ebortsov.deezermusicplayer

import com.github.ebortsov.deezermusicplayer.data.deezerapi.DeezerApiDataSource
import com.github.ebortsov.deezermusicplayer.model.Album
import com.github.ebortsov.deezermusicplayer.model.Artist
import com.github.ebortsov.deezermusicplayer.model.Track
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.assertEquals
import java.net.URI

class DeezerApiDataSourceTests {
    private val deezerApiDataSource = DeezerApiDataSource.createUsingRetrofit()

    @Test
    fun `simple search does not throw exception`() {
        runBlocking {
            deezerApiDataSource.searchTracks("aespa")
        }
    }

    @Test
    fun `meaningless search returns empty result`() {
        val expectedResult = listOf<Track>()
        val result = runBlocking {
            deezerApiDataSource.searchTracks("ssdasdd asdasddadsi asodasd") // some meaningless query
        }
        assertEquals(expectedResult, result)
    }

    @Test
    fun `get track by id returns correct track`() {
        val stubUri = URI.create("example.com")

        val expectedTrack = Track(
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
        val resultTrack = runBlocking {
            deezerApiDataSource.getTrack(2394714525)
        }

        // Now compare everything
        assertEquals(expectedTrack.id, resultTrack.id)
        assertEquals(expectedTrack.artist.id, resultTrack.artist.id)
        assertEquals(expectedTrack.album.id, resultTrack.album.id)
    }
}