package com.github.ebortsov.deezermusicplayer.data.local

import com.github.ebortsov.deezermusicplayer.model.Track

/**
 * Interface that represents a service for managing tracks locally on the user's device
 */
interface TracksLocalDataSource {
    suspend fun downloadTrack(track: Track): Boolean // returns true if success, false otherwise
    suspend fun getTracks(): List<Track>
    suspend fun removeTrack(track: Track): Boolean // returns true if success, false otherwise
}