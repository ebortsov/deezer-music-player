package com.github.ebortsov.deezermusicplayer.player

import android.util.Log
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

private const val TAG = "PlaybackService"

class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        Log.i(TAG, "onCreate")
        super.onCreate()
        initializePlayer()
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    // Set up Media Session
    private fun initializePlayer() {
        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()
    }

    // Release resources associated with MediaSession
    private fun releasePlayer() {
        mediaSession?.apply {
            player.release()
            release()
        }
        mediaSession = null
    }
}