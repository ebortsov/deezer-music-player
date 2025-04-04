package com.github.ebortsov.deezermusicplayer

import android.app.Application
import com.github.ebortsov.deezermusicplayer.download.general.FileDownloader

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        FileDownloader.initialize()
    }
}