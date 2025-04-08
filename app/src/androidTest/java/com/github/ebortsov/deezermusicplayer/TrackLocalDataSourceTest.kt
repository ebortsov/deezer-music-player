package com.github.ebortsov.deezermusicplayer

import android.os.Environment
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.github.ebortsov.deezermusicplayer.data.local.TrackDownloadWorkerFactory
import com.github.ebortsov.deezermusicplayer.data.local.TracksLocalDataSource
import com.github.ebortsov.deezermusicplayer.data.local.TracksLocalDataSourceFile
import com.github.ebortsov.deezermusicplayer.download.FileDownloader
import com.github.ebortsov.deezermusicplayer.model.Track
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

    private val destinationFolder =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .resolve("local-data-source-test")

    private val tracksDestination = destinationFolder.resolve("tracks")
    private val trackCoversDestination = destinationFolder.resolve("track-covers")
    private val trackJsonsDestination = destinationFolder.resolve("track-jsons")

    private val trackLocalDataSource: TracksLocalDataSource

    private val fileDownloader = FileDownloader()

    init {
        destinationFolder.mkdirs()

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Create work manager
        val workerFactory = DelegatingWorkerFactory()
        workerFactory.addFactory(TrackDownloadWorkerFactory(fileDownloader))
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        trackLocalDataSource = TracksLocalDataSourceFile(
            WorkManager.getInstance(context),
            tracksDestination = tracksDestination.toPath(),
            trackCoversDestination = trackCoversDestination.toPath(),
            trackJsonsDestination = trackJsonsDestination.toPath()
        )
    }


    @Test
    fun basic_crud_works_correctly() {
        runBlocking {
            // Insert sample tracks
            val downloads = trackStubs.map { track ->
                async { trackLocalDataSource.downloadTrack(track) }
            }
            trackLocalDataSource.downloadTrack(trackStubs[2]) // Repeating download shouldn't cause any errors
            downloads.awaitAll()

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