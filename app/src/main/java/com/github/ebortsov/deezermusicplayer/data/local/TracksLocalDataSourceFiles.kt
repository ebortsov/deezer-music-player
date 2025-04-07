package com.github.ebortsov.deezermusicplayer.data.local

import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.github.ebortsov.deezermusicplayer.download.general.FileDownloader
import com.github.ebortsov.deezermusicplayer.model.Track
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.io.File

private const val TAG = "TracksLocalFilesDataSource"

class TracksLocalFilesDataSource(
    private val workManager: WorkManager,
    private val fileDownloader: FileDownloader,
    private val tracksDestination: File,
    private val trackCoversDestination: File,
    private val trackJsonsDestination: File
) : TracksLocalDataSource {
    override suspend fun downloadTrack(track: Track): Boolean {
        // We cannot know which file type corresponds to the track (flac, mp3 etc.)
        val trackDestinationUri = tracksDestination.resolve("${track.id}-track").toURI()

        // We cannot know which file type corresponds to the track cover (jpg, png etc.)
        val trackCoverDestinationUri = tracksDestination.resolve("${track.id}-cover").toURI()

        val trackJsonDestinationUri = tracksDestination.resolve("${track.id}.json").toURI()

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

    override suspend fun getTracks(): List<Track> {

    }

    override suspend fun removeTrack(track: Track): Boolean {

    }

}