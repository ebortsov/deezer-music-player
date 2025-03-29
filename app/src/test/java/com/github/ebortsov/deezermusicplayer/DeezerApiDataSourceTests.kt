package com.github.ebortsov.deezermusicplayer

import com.github.ebortsov.deezermusicplayer.data.deezerapi.DeezerApiDataSource
import com.github.ebortsov.deezermusicplayer.data.deezerapi.apimodels.SearchResultApiModel
import com.github.ebortsov.deezermusicplayer.data.deezerapi.apimodels.SearchResultItemApiModel
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
        val expectedResult = listOf<SearchResultItemApiModel>()
        val result = runBlocking {
            deezerApiDataSource.searchTracks("ssdasdd asdasddadsi asodasd") // some meaningless query
        }
        assertEquals(expectedResult, result)
    }

    @Test
    fun `get track by id returns correct track`() {
        val expectedTrack = Track(
            "3030102541",
            "Whiplash",
            URI.create("example.com") // some stub link
        )
        val resultTrack = runBlocking {
            deezerApiDataSource.getTrack("3030102541")
        }
        assertEquals(expectedTrack.id, resultTrack.id)
        assertEquals(expectedTrack.title, resultTrack.title)
    }
}