package com.github.ebortsov.deezermusicplayer.di

import android.content.Context
import android.os.Environment
import androidx.work.WorkManager
import com.github.ebortsov.deezermusicplayer.data.TracksRepository
import com.github.ebortsov.deezermusicplayer.data.local.TracksLocalDataSourceFile
import com.github.ebortsov.deezermusicplayer.data.remote.TracksRemoteDataSourceDeezer
import com.github.ebortsov.deezermusicplayer.download.FileDownloader
import com.github.ebortsov.deezermusicplayer.network.deezer.createDeezerApiService

class DefaultAppContainer(appContext: Context) : AppContainer {
    override val fileDownloader = FileDownloader()

    // Initialize the local data source
    private val tracksDestination =
        appContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC)!!.toPath()
    private val trackCoversDestination =
        appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.toPath()
    private val trackJsonsDestination =
        appContext.getExternalFilesDir(null)!!.resolve("tracks-info").toPath()

    // Note the usage of `lazy` here
    // It is a must here since the WorkManager uses custom configuration (which is configured also in Application class)
    private val workManager by lazy { WorkManager.getInstance(appContext) }
    private val tracksLocalDataSource by lazy {
        TracksLocalDataSourceFile(
            workManager,
            tracksDestination = tracksDestination,
            trackCoversDestination = trackCoversDestination,
            trackJsonsDestination = trackJsonsDestination
        )
    }

    // Initialize the remote data source
    private val deezerApiService = createDeezerApiService()
    private val tracksRemoteDataSource = TracksRemoteDataSourceDeezer(deezerApiService)

    // Initialize the tracks repository
    override val tracksRepository: TracksRepository by lazy {
        TracksRepository(
            tracksLocalDataSource,
            tracksRemoteDataSource
        )
    }
}