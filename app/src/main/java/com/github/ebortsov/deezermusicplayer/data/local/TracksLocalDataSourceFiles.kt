package com.github.ebortsov.deezermusicplayer.data.local

import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.github.ebortsov.deezermusicplayer.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path

private const val TAG = "TracksLocalDataSourceFile"

class TracksLocalDataSourceFile(
    private val workManager: WorkManager,
    private val tracksDestination: Path,
    private val trackCoversDestination: Path,
    private val trackJsonsDestination: Path
) : TracksLocalDataSource {
    init {
        // Create the folders for the destinations if they don't exist
        for (destination in listOf(
            tracksDestination,
            trackJsonsDestination,
            trackCoversDestination
        )) {
            Files.createDirectories(destination)
            check(Files.isWritable(destination))
        }

        // All directories must be different
        check(!Files.isSameFile(tracksDestination, trackCoversDestination))
        check(!Files.isSameFile(tracksDestination, trackJsonsDestination))
        check(!Files.isSameFile(trackJsonsDestination, trackCoversDestination))
    }

    override suspend fun downloadTrack(track: Track): Boolean {
        val trackDestinationUri = tracksDestination.resolve("${track.id}-track").toUri()
        val trackCoverDestinationUri = trackCoversDestination.resolve("${track.id}-cover").toUri()
        val trackJsonDestinationUri = trackJsonsDestination.resolve("${track.id}.json").toUri()

        val workRequest = OneTimeWorkRequestBuilder<TrackDownloadWorker>()
            .setInputData(
                workDataOf(
                    KEY_TRACK to Json.encodeToString<Track>(track),
                    KEY_TRACK_DESTINATION_URI to trackDestinationUri.toString(),
                    KEY_TRACK_COVER_DESTINATION_URI to trackCoverDestinationUri.toString(),
                    KEY_TRACK_JSON_DESTINATION_URI to trackJsonDestinationUri.toString()
                )
            )
            .build()

        // Start the worker
        workManager.enqueue(workRequest)

        // Monitor the worker state until it reaches the SUCCESS
        try {
            val workInfo = workManager
                .getWorkInfoByIdFlow(workRequest.id)
                .filterNotNull()
                .first { it.state.isFinished }

            return workInfo.state == WorkInfo.State.SUCCEEDED
        } catch (ex: Exception) {
            Log.e(TAG, "downloadTrack: $ex")
            return false
        }
    }

    override suspend fun getTracks(): List<Track> = withContext(Dispatchers.IO) {
        try {
            val result = mutableListOf<Track>()
            Files.newDirectoryStream(trackJsonsDestination).use { stream ->
                for (trackJson in stream) {
                    val track = Json.decodeFromString<Track>(
                        String(Files.readAllBytes(trackJson))
                    )
                    result.add(track)
                }
            }
            result
        } catch (ex: Exception) {
            Log.e(TAG, "getTracks: $ex")
            listOf()
        }
    }

    override suspend fun removeTrack(track: Track): Boolean = withContext(Dispatchers.IO) {
        try {
            val trackDestination = tracksDestination.resolve("${track.id}-track")
            val trackCoverDestination = trackCoversDestination.resolve("${track.id}-cover")
            val trackJsonDestination = trackJsonsDestination.resolve("${track.id}.json")

            Files.deleteIfExists(trackJsonDestination)
            Files.deleteIfExists(trackDestination)
            Files.deleteIfExists(trackCoverDestination)

            Log.i(TAG, "Track $track is deleted")
            true
        } catch (ex: Exception) {
            false
        }
    }
}