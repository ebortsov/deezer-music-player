package com.github.ebortsov.deezermusicplayer.download

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.github.ebortsov.deezermusicplayer.download.database.TrackInfoDataSource
import com.github.ebortsov.deezermusicplayer.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.guava.await
import kotlinx.serialization.json.Json
import java.io.File

private const val TAG = "TrackLocalDataSource"

/**
 * DataSource class that manages the tracks download
 */
class TrackLocalDataSource private constructor(
    private val workManager: WorkManager,
    private val trackInfoDataSource: TrackInfoDataSource,
    val tracksDownloadDir: File,
    val trackCoversDownloadDir: File
) {
    init {
        check(tracksDownloadDir.isDirectory && tracksDownloadDir.canWrite())
        check(trackCoversDownloadDir.isDirectory && trackCoversDownloadDir.canWrite())
    }
    suspend fun downloadTrack(track: Track): Boolean {
        val trackDestination = getTrackRelatedItemFile(tracksDownloadDir, track) // where to save the track itself
        val trackCoverDestination = getTrackRelatedItemFile(trackCoversDownloadDir, track) // where to save the track cover

        // Start the work request to download the files and then store the track in the database
        val workRequest = OneTimeWorkRequestBuilder<TrackDownloadWorker>()
            .setInputData(
                workDataOf(
                    KEY_TRACK to Json.encodeToString(track), // Json-encoded track
                    KEY_TRACK_DESTINATION_URI to Uri.fromFile(trackDestination).toString(),
                    KEY_TRACK_COVER_DESTINATION_URI to Uri.fromFile(trackCoverDestination)
                        .toString()
                )
            )
            .build()

        return try {
            workManager.enqueue(workRequest).result.await()
            true
        } catch (ex: Exception) {
            return false // Worker returned Result.failure()
        }
    }

    suspend fun getTracks(): List<Track> {
        return trackInfoDataSource.getTracks()
    }

    suspend fun getTracksAsFlow(): Flow<List<Track>> {
        return trackInfoDataSource.getTracksAsFlow()
    }

    fun getTrackRelatedItemFile(baseDir: File, track: Track, suffix: String = ""): File {
        return baseDir.resolve("${track.id}$suffix")
    }

    companion object {
        private var instance: TrackLocalDataSource? = null

        fun initialize(
            appContext: Context,
            trackInfoDataSource: TrackInfoDataSource,
            tracksDownloadDir: File,
            trackCoversDownloadDir: File
        ) {
            val workManager = appContext.getSystemService(WorkManager::class.java) as WorkManager
            instance = TrackLocalDataSource(
                workManager, trackInfoDataSource, tracksDownloadDir, trackCoversDownloadDir
            )
        }

        fun getInstance(): TrackLocalDataSource {
            return checkNotNull(instance) { "TrackLocalDataSource must be initialized" }
        }
    }
}