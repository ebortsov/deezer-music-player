package com.github.ebortsov.deezermusicplayer.player

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch


private const val TAG = "PlaybackServiceManager"

/**
 * Lifecycle aware class that acts as an
 * disposable provider and initializer of the MediaController
 *
 * Brief description
 * 1) lifecycleOwner reached the ON_START state
 *
 * 2) MediaController is initialized and the onControllerIsReadyListener is called (if defined)
 * The initialized MediaController is passed to the onControllerIsReadyListener
 *
 * 3) The lifecycleOwner reaches the ON_STOP state. The MediaController resources are released
 * and any instance fetched with this class (i.e. passed to the callback) is no longer valid
 */

class PlaybackServiceManager(
    lifecycleOwner: LifecycleOwner,
    private val context: Context
) : DefaultLifecycleObserver {
    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    private var onControllerIsReadyListener: OnControllerIsReadyListener? = null

    override fun onStart(owner: LifecycleOwner) {
        owner.lifecycleScope.launch {
            initializeController()

            onControllerIsReadyListener?.onControllerReady(controller!!)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        // Release the controller when the `lifecycleOwner` reaches the `ON_STOP` state
        releaseController()
        owner.lifecycle.removeObserver(this)
    }

    private suspend fun initializeController() {
        Log.d(TAG, "initializeController")

        // Create the ControllerFuture and retrieve the MediaController associated with it
        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controller = controllerFuture?.await()

    }

    private fun releaseController() {
        Log.d(TAG, "releaseController")

        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        controller = null
    }

    fun setOnControllerIsReadyListener(listener: OnControllerIsReadyListener) {
        onControllerIsReadyListener = listener
        if (controller != null) {
            onControllerIsReadyListener?.onControllerReady(controller!!)
        }
    }
}

fun interface OnControllerIsReadyListener {
    fun onControllerReady(mediaController: MediaController)
}