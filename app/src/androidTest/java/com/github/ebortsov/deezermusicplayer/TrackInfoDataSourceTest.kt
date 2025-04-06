package com.github.ebortsov.deezermusicplayer

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.ebortsov.deezermusicplayer.download.database.TrackInfoDataSource
import com.github.ebortsov.deezermusicplayer.download.database.TrackInfoDatabase
import com.github.ebortsov.deezermusicplayer.model.Track
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Before

@RunWith(AndroidJUnit4::class)
class TrackInfoDataSourceTest {
    private lateinit var database: TrackInfoDatabase
    private lateinit var trackInfoDataSource: TrackInfoDataSource
    private val tracks = createStubTracks()

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().context
        database = Room.inMemoryDatabaseBuilder(
            context,
            TrackInfoDatabase::class.java
        ).build()

        trackInfoDataSource = TrackInfoDataSource(database)
    }

    @Test
    fun clear_all_works_correctly() {
        runBlocking {
            trackInfoDataSource.insertTrack(tracks[0])
            trackInfoDataSource.insertTrack(tracks[1])
            trackInfoDataSource.clearTracks()
            verifyDatabaseContent(listOf())
        }
    }

    // Test that includes various operations for working with database
    @Test
    fun crud_works_correctly() {
        runBlocking {
            trackInfoDataSource.insertTrack(tracks[0])
            trackInfoDataSource.insertTrack(tracks[1])
            trackInfoDataSource.insertTrack(tracks[0]) // Repeating insert should simply replace the item

            verifyDatabaseContent(listOf(tracks[0], tracks[1]))

            trackInfoDataSource.removeTrack(tracks[0])
            trackInfoDataSource.removeTrack(tracks[2]) // remove track that isn't present
            trackInfoDataSource.removeTrack(tracks[0]) // repeating remove shouldn't cause any errors

            verifyDatabaseContent(listOf(tracks[1]))

            val insertedTracks = trackInfoDataSource.getTracks()
            assertFalse(tracks[0] in insertedTracks)

            assertFalse(trackInfoDataSource.isPresent(tracks[0]))
            assertTrue(trackInfoDataSource.isPresent(tracks[1]))
        }
    }

    private suspend fun verifyDatabaseContent(expectedTracks: List<Track>) {
        val insertedTracks = trackInfoDataSource.getTracks()
        assertEquals(expectedTracks.size, insertedTracks.size)
        for (track in expectedTracks) {
            assertTrue(track in insertedTracks)
        }
    }
}