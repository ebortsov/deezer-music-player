package com.github.ebortsov.deezermusicplayer.data.local

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.ebortsov.deezermusicplayer.download.general.FileDownloader
import com.github.ebortsov.deezermusicplayer.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.PrintWriter
import java.net.URI

private const val TAG = "TrackDownloadWorker"

const val KEY_TRACK_DESTINATION_URI = "KEY_TRACK_DESTINATION_URI"
const val KEY_TRACK_COVER_DESTINATION_URI = "KEY_TRACK_COVER_DESTINATION_URI"
const val KEY_TRACK_JSON_DESTINATION_URI = "KEY_TRACK_JSON_DESTINATION_URI"
const val KEY_TRACK = "KEY_TRACK"

class TrackDownloadWorker(
    private val fileDownloader: FileDownloader,
    private val appContext: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(appContext, workerParameters) {
    /**
     * The download process takes three steps:
     * 1) Download the track itself
     * 2) Download the track cover
     * 3) If the (1) and (2) are successful, then record the store the track as json file
     */
    override suspend fun doWork(): Result {
        val track: Track
        val trackDestination: String
        val trackCoverDestination: String
        val trackJsonDestination: String

        try {
            trackDestination = inputData.getString(KEY_TRACK_DESTINATION_URI)!!
            trackCoverDestination = inputData.getString(KEY_TRACK_COVER_DESTINATION_URI)!!
            trackJsonDestination = inputData.getString(KEY_TRACK_JSON_DESTINATION_URI)!!

            track = Json.decodeFromString<Track>(inputData.getString(KEY_TRACK)!!)
        } catch (ex: Exception) {
            Log.e(TAG, "doWork: $ex")
            return Result.failure()
        }

        return withContext(Dispatchers.IO) {
            val trackDownloadJob = async {
                fileDownloader.download(
                    File(URI.create(trackDestination)),
                    track.previewUri.toString()
                )
            }

            val trackCoverDownloadJob = async {
                fileDownloader.download(
                    File(URI.create(trackCoverDestination)),
                    track.album.coverUri.toString()
                )
            }
            val trackDownloadResult = trackDownloadJob.await()
            val trackCoverDownloadResult = trackCoverDownloadJob.await()

            if (trackDownloadResult && trackCoverDownloadResult) {
                val localTrack = track.copy(
                    previewUri = URI.create(trackDestination),
                    album = track.album.copy(coverUri = URI.create(trackDestination))
                )

                // Save the file as json file
                val trackJsonFile = File(URI.create(trackJsonDestination))
                val trackJsonFileWriter = PrintWriter(trackJsonFile)
                trackJsonFileWriter.use { writer ->
                    writer.write(
                        Json.encodeToString<Track>(localTrack)
                    )
                }

                Log.i(TAG, "doWork: success")
                Result.success()
            } else {
                Log.i(
                    TAG,
                    "doWork: failure; trackDownloadResult = $trackDownloadResult; trackCoverDownloadResult = $trackCoverDownloadResult"
                )
                Result.failure()
            }
        }
    }
}