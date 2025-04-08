package com.github.ebortsov.deezermusicplayer.presentation.tracklist.savedtracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.ebortsov.deezermusicplayer.App
import com.github.ebortsov.deezermusicplayer.data.TracksRepository
import com.github.ebortsov.deezermusicplayer.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val savedTracks: List<Track>,
    val searchQuery: String
)

private const val TAG = "SavedTracksViewModel"

class SavedTracksViewModel(
    private val tracksRepository: TracksRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        UiState(
            listOf(),
            ""
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val savedTracks = tracksRepository.getLocalTracks()
            _uiState.update { it.copy(savedTracks = savedTracks) }
        }
    }

    fun searchTracks(query: String) {
        viewModelScope.launch {
            val tracks = tracksRepository.searchTrackInLocal(query)
            _uiState.update { it.copy(savedTracks = tracks) }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val tracksRepository = (this[APPLICATION_KEY] as App).appContainer.tracksRepository
                SavedTracksViewModel(tracksRepository)
            }
        }
    }
}