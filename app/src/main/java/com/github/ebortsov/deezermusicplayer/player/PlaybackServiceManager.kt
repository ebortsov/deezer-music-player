package com.github.ebortsov.deezermusicplayer.player

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch


private const val TAG = "PlaybackServiceManager"

// TODO: handle potential exceptions
// TODO: add comments
/**
 * Lifecycle aware class that acts as an
 * interlayer between the Service and some other component (Fragment or Activity)
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

    override fun onCreate(owner: LifecycleOwner) {
        owner.lifecycleScope.launch {
            owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                initializeController()

                // Run the listener once the controller has been created
                onControllerIsReadyListener?.onControllerReady(controller!!)
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        // Release the controller when the `lifecycleOwner` reaches the `ON_STOP` state
        releaseController()
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