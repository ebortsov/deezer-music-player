package com.github.ebortsov.deezermusicplayer.screens.playback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import com.github.ebortsov.deezermusicplayer.databinding.FragmentPlaybackBinding
import com.github.ebortsov.deezermusicplayer.player.PlaybackServiceManager
import com.github.ebortsov.deezermusicplayer.R
import com.github.ebortsov.deezermusicplayer.utils.formatMilliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.roundToLong

private const val SEEKBAR_MAX = 1000

/*
* Screen that connects to the `PlaybackService` and show the current playback state
* */
class PlaybackFragment : Fragment() {
    private val playbackServiceManager by lazy {
        PlaybackServiceManager(this, requireContext())
    }

    private lateinit var binding: FragmentPlaybackBinding

    // Flag that shows if the user is currently dragging the seekbar
    // Gets reset on every `ON_START` state)
    private var isUserUsingSeekbar = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaybackBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playbackServiceManager.setOnControllerIsReadyListener { mediaController ->
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    // Reset the seekbar interaction flag
                    isUserUsingSeekbar = false

                    startMonitoringPlayer(mediaController)
                }
            }

            // Add necessary listeners to the buttons
            setupUiControls(mediaController)
        }
    }


    // Run with some interval and update the UI based on the player state
    private suspend fun startMonitoringPlayer(
        mediaController: MediaController,
        pollDelayMs: Long = 50L
    ) {
        while (true) {
            updateUi(mediaController)
            delay(pollDelayMs)
        }
    }

    private fun setupUiControls(mediaController: MediaController) {
        binding.seekbar.max = SEEKBAR_MAX

        // Pause/resume the playback based on its status
        binding.pauseButton.setOnClickListener {
            if (mediaController.isPlaying)
                mediaController.pause()
            else
                mediaController.play()
        }

        // Set the playback position using the seekbar
        // (the mediaController is notified once the user ends interacting with the seekbar)
        binding.seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Mark that the seekbar is in usage to prevent updating it
                isUserUsingSeekbar = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Once the user stops interacting with seekbar, seek the player into the position
                // calculated from the seekbar state
                val newMediaPositionMs =
                    mediaController.duration * (seekBar.progress.toDouble() / seekBar.max)
                mediaController.seekTo(newMediaPositionMs.roundToLong())

                // Reset the flag since the user stops interacting with it
                isUserUsingSeekbar = false
            }
        })

        // Start next track
        binding.skipNextButton.setOnClickListener {
            mediaController.seekToNext()
        }

        // Start previous track
        binding.skipPreviousButton.setOnClickListener {
            mediaController.seekToPrevious()
        }
    }

    private fun updateUiMetadata(metadata: MediaMetadata) {
        binding.trackTitleTextView.text = metadata.title ?: ""
        binding.artistNameTextView.text = metadata.artist ?: ""
        binding.albumTitleTextView.text = metadata.albumTitle ?: ""

    }

    private fun updateUi(mediaController: MediaController) {
        // Change the image on the pause button depending on the current playback state
        binding.pauseButton.setImageResource(
            if (mediaController.playWhenReady) R.drawable.ic_pause
            else R.drawable.ic_play
        )

        // Change the seekbar position
        if (!isUserUsingSeekbar) {
            val progress =
                SEEKBAR_MAX * (mediaController.currentPosition.toDouble() / mediaController.duration)
            binding.seekbar.progress = progress.roundToInt()
        }

        // Format the current track position
        binding.currentPositionTextView.text = formatMilliseconds(mediaController.currentPosition)

        // Format the remaining track time
        binding.trackDurationTextView.text = if (mediaController.duration != C.TIME_UNSET) {
            formatMilliseconds(mediaController.duration)
        } else {
            ""
        }

        updateUiMetadata(mediaController.mediaMetadata)

    }
}