package com.github.ebortsov.deezermusicplayer.di

import com.github.ebortsov.deezermusicplayer.data.TracksRepository

interface AppContainer {
    val tracksRepository: TracksRepository
}