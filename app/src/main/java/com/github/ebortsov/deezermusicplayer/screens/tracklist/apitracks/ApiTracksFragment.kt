package com.github.ebortsov.deezermusicplayer.screens.tracklist.apitracks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
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
import com.github.ebortsov.deezermusicplayer.R
import com.github.ebortsov.deezermusicplayer.databinding.FragmentTracksBinding
import com.github.ebortsov.deezermusicplayer.player.PlaybackServiceManager
import com.github.ebortsov.deezermusicplayer.player.createMediaItemFromTrack
import com.github.ebortsov.deezermusicplayer.screens.tracklist.adapter.OnTrackClickListener
import com.github.ebortsov.deezermusicplayer.screens.tracklist.adapter.TrackListAdapter
import com.github.ebortsov.deezermusicplayer.screens.playback.PlaybackDestination
import com.github.ebortsov.deezermusicplayer.screens.tracklist.adapter.OnTrackDownloadListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object ApiTracksDestination

fun NavGraphBuilder.apiTracksDestination() {
    fragment<ApiTracksFragment, ApiTracksDestination>()
}

class ApiTracksFragment : Fragment() {
    private lateinit var binding: FragmentTracksBinding
    private lateinit var trackListAdapter: TrackListAdapter
    private val viewModel: ApiTracksViewModel by viewModels()
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

        val onTrackDownloadListener = OnTrackDownloadListener { _, track ->
            viewModel.downloadTrack(track)
        }

        // Configure recycler view
        with(binding.tracksRecyclerView) {
            layoutManager = LinearLayoutManager(requireActivity())

            trackListAdapter = TrackListAdapter(onTrackClickListener, onTrackDownloadListener)
            binding.tracksRecyclerView.adapter = trackListAdapter
        }

        // Configure the "search" action on the search view
        binding.searchView.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchTracks(v.text.toString())

                // Close soft keyboard
                hideKeyboard()

                // Drop the focus from the search field
                v.clearFocus()
                true
            } else {
                false
            }
        }
    }

    private fun hideKeyboard() {
        // Astonishing api for hiding the keyboard
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus
        view?.let {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun updateUi(uiState: UiState) {
        // Whenever the uiState changes this method is called

        // Update the displayed search query in search view
        binding.searchView.setText(uiState.searchQuery)

        // Send the fetched to the recycler view
        trackListAdapter.submitList(uiState.loadedTracks)

        // Handle the loading state
        when (uiState.loadingState) {
            UiState.LoadingState.IDLE -> {
                // Hide the progress indicator
                binding.loadingProgressIndicator.isVisible = false
            }

            UiState.LoadingState.LOADING -> {
                // Show the progress indicator
                binding.loadingProgressIndicator.isVisible = true
            }

            UiState.LoadingState.ERROR -> {
                // Hide the progress indicator
                binding.loadingProgressIndicator.isVisible = false

                // Show snackbar with error message
                Snackbar.make(
                    binding.tracksRecyclerView,
                    resources.getString(R.string.error_message_snackbar),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun searchTracks(query: String) {
        viewModel.searchTracks(query)
    }
}


