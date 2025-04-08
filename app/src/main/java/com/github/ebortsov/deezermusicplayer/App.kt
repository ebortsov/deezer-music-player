package com.github.ebortsov.deezermusicplayer

import android.app.Application
import android.os.Environment
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.github.ebortsov.deezermusicplayer.data.local.TrackDownloadWorkerFactory
import com.github.ebortsov.deezermusicplayer.di.AppContainer
import com.github.ebortsov.deezermusicplayer.di.DefaultAppContainer

class App : Application(), Configuration.Provider {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()

        appContainer = DefaultAppContainer(applicationContext)
    }

    override val workManagerConfiguration: Configuration get() {
        val workerFactory = DelegatingWorkerFactory()
        workerFactory.addFactory(TrackDownloadWorkerFactory(appContainer.fileDownloader))

        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()
    }
}