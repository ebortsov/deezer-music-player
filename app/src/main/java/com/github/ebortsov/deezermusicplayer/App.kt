package com.github.ebortsov.deezermusicplayer

import android.app.Application
import android.os.Environment
import com.github.ebortsov.deezermusicplayer.download.TrackLocalDataSource
import com.github.ebortsov.deezermusicplayer.download.database.TrackInfoDataSource
import com.github.ebortsov.deezermusicplayer.download.database.TrackInfoDatabase
import com.github.ebortsov.deezermusicplayer.download.general.FileDownloader

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        TrackInfoDatabase.initialize(applicationContext)
        FileDownloader.initialize()

        TrackLocalDataSource.initialize(
            applicationContext,
            TrackInfoDataSource(TrackInfoDatabase.getInstance()),
            applicationContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC)!!.resolve(applicationContext.packageName),
            applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.resolve(applicationContext.packageName)
        )
    }
}