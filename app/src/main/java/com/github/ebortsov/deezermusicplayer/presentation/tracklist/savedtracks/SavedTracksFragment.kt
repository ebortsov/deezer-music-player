package com.github.ebortsov.deezermusicplayer.presentation.tracklist.savedtracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.session.MediaController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ebortsov.deezermusicplayer.databinding.FragmentTracksBinding
import com.github.ebortsov.deezermusicplayer.player.PlaybackServiceManager
import com.github.ebortsov.deezermusicplayer.player.createMediaItemFromTrack
import com.github.ebortsov.deezermusicplayer.presentation.playback.PlaybackDestination
import com.github.ebortsov.deezermusicplayer.presentation.tracklist.adapter.OnTrackClickListener
import com.github.ebortsov.deezermusicplayer.presentation.tracklist.adapter.OnTrackDownloadListener
import com.github.ebortsov.deezermusicplayer.presentation.tracklist.adapter.TrackListAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object SavedTracksDestination

fun NavGraphBuilder.savedTracksDestination() {
    fragment<SavedTracksFragment, SavedTracksDestination>()
}

class SavedTracksFragment : Fragment() {
    private lateinit var binding: FragmentTracksBinding
    private lateinit var trackListAdapter: TrackListAdapter
    private val viewModel: SavedTracksViewModel by viewModels { SavedTracksViewModel.Factory }
    private val playbackServiceManager by lazy {
        PlaybackServiceManager(this, requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playbackServiceManager.setOnControllerIsReadyListener { mediaController ->
            setupUiControls(mediaController)

            // Start tracking the state and update the views correspondingly
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiState.collectLatest { uiState ->
                        updateUi(uiState)
                    }
                }
            }
        }
    }

    private fun setupUiControls(mediaController: MediaController) {
        val onTrackClickListener = OnTrackClickListener { _, track ->
            // setup playlist to consist of only this song
            mediaController.clearMediaItems()
            mediaController.addMediaItem(createMediaItemFromTrack(track))

            // Start the playback
            mediaController.play()

            // Navigate to player screen
            findNavController().navigate(PlaybackDestination)
        }

        val onTrackDownloadListener = OnTrackDownloadListener { _, _ -> }

        // Configure recycler view
        with(binding.tracksRecyclerView) {
            layoutManager = LinearLayoutManager(requireActivity())

            trackListAdapter = TrackListAdapter(onTrackClickListener, onTrackDownloadListener)
            binding.tracksRecyclerView.adapter = trackListAdapter
        }
    }

    private fun updateUi(uiState: UiState) {
        // Whenever the uiState changes this method is called

        // Update the displayed search query in search view
        binding.searchView.setText(uiState.searchQuery)

        // Send the currently stored tracks to the recycler view
        trackListAdapter.submitList(uiState.savedTracks)
    }
}