package com.github.ebortsov.deezermusicplayer.di

import com.github.ebortsov.deezermusicplayer.data.TracksRepository
import com.github.ebortsov.deezermusicplayer.download.FileDownloader

interface AppContainer {
    val tracksRepository: TracksRepository
    val fileDownloader: FileDownloader
}