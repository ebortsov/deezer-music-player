package com.github.ebortsov.deezermusicplayer.download

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.github.ebortsov.deezermusicplayer.download.database.TrackInfoDataSource
import com.github.ebortsov.deezermusicplayer.download.database.TrackInfoDatabase
import com.github.ebortsov.deezermusicplayer.download.general.FileDownloader
import com.github.ebortsov.deezermusicplayer.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URI

private const val TAG = "TrackDownloadWorker"

const val KEY_TRACK_DESTINATION_URI = "KEY_TRACK_DESTINATION_URI"
const val KEY_TRACK_COVER_DESTINATION_URI = "KEY_TRACK_COVER_DESTINATION_URI"
const val KEY_TRACK = "KEY_TRACK"

class TrackDownloadWorker(
    private val appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {
    /**
     * The download process takes three steps:
     * 1) Download the track itself
     * 2) Download the track cover
     * 3) If the (1) and (2) are successful, then record the information about the track to the [TrackInfoDataSource]
     */
    override suspend fun doWork(): Result {
        val trackInfoDataSource = TrackInfoDataSource(TrackInfoDatabase.getInstance())
        val fileDownloader = FileDownloader.getInstance()

        val track: Track
        val trackDestination: String
        val trackCoverDestination: String
        try {
            trackDestination = inputData.getString(KEY_TRACK_DESTINATION_URI)!!
            trackCoverDestination = inputData.getString(KEY_TRACK_COVER_DESTINATION_URI)!!
            track = Json.decodeFromString<Track>(inputData.getString(KEY_TRACK)!!)
        } catch (ex: Exception) {
            Log.e(TAG, "doWork: $ex")
            return Result.failure()
        }

        withContext(Dispatchers.IO) {
            val trackDownloadJob = async {
                fileDownloader.download(File(URI.create(trackDestination)), track.previewUri.toString())
            }
            val trackCoverDownloadJob = async {
                fileDownloader.download(File(URI.create(trackCoverDestination)), track.album.coverUri.toString())
            }

            if (trackDownloadJob.await() && trackCoverDownloadJob.await()) {
                val localTrack = track.copy(
                    previewUri = URI.create(trackDestination),
                    album = track.album.copy(coverUri = URI.create(trackDestination))
                )
                trackInfoDataSource.insertTrack(localTrack)
            }
        }
        return Result.success()
    }
}