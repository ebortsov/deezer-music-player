package com.github.ebortsov.deezermusicplayer

import android.os.Environment
import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.github.ebortsov.deezermusicplayer.download.TrackLocalDataSource
import com.github.ebortsov.deezermusicplayer.download.database.TrackInfoDataSource
import com.github.ebortsov.deezermusicplayer.download.database.TrackInfoDatabase
import com.github.ebortsov.deezermusicplayer.download.general.FileDownloader
import com.github.ebortsov.deezermusicplayer.model.Track
import io.mockk.every
import io.mockk.mockkObject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.io.File


@RunWith(AndroidJUnit4::class)
class TrackLocalDataSourceTest {
    private val trackStubs = createStubTracks()
    private val trackInfoDataSource: TrackInfoDataSource
    private val trackLocalDataSource: TrackLocalDataSource
    private val destinationFolder =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .resolve("local-data-source-test")
    private val tracksDestination = destinationFolder.resolve("tracks")
    private val trackCoversDestination = destinationFolder.resolve("track-covers")
    private val fileDownloader: FileDownloader

    init {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Create work manager
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        // Initialize in memory database
        val database = Room.inMemoryDatabaseBuilder(
            context,
            TrackInfoDatabase::class.java
        ).build()

        // Initialize TrackInfoDataSource
        trackInfoDataSource = TrackInfoDataSource(database)

        // Ensure the folders for tracks and covers exist
        tracksDestination.mkdirs()
        trackCoversDestination.mkdirs()

        // Initialize TrackLocalDataSource
        TrackLocalDataSource.initialize(
            context,
            trackInfoDataSource,
            tracksDestination,
            trackCoversDestination
        )
        trackLocalDataSource = TrackLocalDataSource.getInstance()

        // Initialize FileDownloader
        FileDownloader.initialize()
        fileDownloader = FileDownloader.getInstance()

        // Mockk the database
        mockkObject(TrackInfoDatabase.Companion)

        // Mockk the getInstance of the TrackInfoDatabase to return the in-memory database
        every { TrackInfoDatabase.getInstance() } returns database
    }


    @Test
    fun basic_crud_works_correctly() {
        runBlocking {
            // Insert sample tracks
            val downloads = trackStubs.map { track ->
                async { trackLocalDataSource.downloadTrack(track) }
            }
            trackLocalDataSource.downloadTrack(trackStubs[2])
            val downloadResults = downloads.awaitAll()

            // All downloads have been successful
            for ((i, downloadResult) in downloadResults.withIndex()) {
                assertTrue("$i-th download", downloadResult)
            }

            checkExpectedTracks(trackStubs)

            trackLocalDataSource.removeTrack(trackStubs[2])
            trackLocalDataSource.removeTrack(trackStubs[2]) // Repeating removal should not cause any errors

            checkExpectedTracks(trackStubs.dropLast(1))

            // Invalid track download must return false
            val invalidTrackDownloadResult = trackLocalDataSource.downloadTrack(invalidTrack)
            assertFalse(invalidTrackDownloadResult)

            // Download of invalid track should not change the existing tracks
            checkExpectedTracks(trackStubs.dropLast(1))
        }
    }

    @After
    fun cleanup() {
        Thread.sleep(500L)
        destinationFolder.deleteRecursively()
    }

    private fun checkExpectedTracks(expectedTracks: List<Track>) = runBlocking {
        // The number of stored tracks is equal to the number of number of downloaded tracks
        val downloadedTracks = trackLocalDataSource.getTracks()
        assertEquals(expectedTracks.size, downloadedTracks.size)

        // Each stored track is correctly fetched from the stored tracks
        for (track in expectedTracks) {
            val localTrack = downloadedTracks.find { track.id == it.id }
            assertTrue("track $track", localTrack != null)
            checkTrackPresence(localTrack!!)
        }
    }

    private fun checkTrackPresence(localTrack: Track) = runBlocking {
        val track = File(localTrack.previewUri)
        val trackCover = File(localTrack.album.coverUri)
        assertTrue(track.exists())
        assertTrue(trackCover.exists())
    }
}