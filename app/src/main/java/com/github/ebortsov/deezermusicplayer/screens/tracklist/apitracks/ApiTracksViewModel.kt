package com.github.ebortsov.deezermusicplayer.screens.tracklist.apitracks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ebortsov.deezermusicplayer.data.TracksRepository
import com.github.ebortsov.deezermusicplayer.download.TrackLocalDataSource
import com.github.ebortsov.deezermusicplayer.model.Track
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class UiState(
    val loadingState: LoadingState,
    val loadedTracks: List<Track>,
    val searchQuery: String,
) {
    enum class LoadingState() { IDLE, ERROR, LOADING; }
}

private const val TAG = "ApiTracksViewModel"

class ApiTracksViewModel(
    private val tracksRepository: TracksRepository = TracksRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        UiState(
            UiState.LoadingState.IDLE,
            listOf(),
            ""
        )
    )
    val uiState = _uiState.asStateFlow()

    fun searchTracks(query: String) {
        // Send the query to the repository and check the result
        viewModelScope.launch {
            _uiState.update { it.copy(searchQuery = query, loadingState = UiState.LoadingState.LOADING) }

            try {
                val tracks = tracksRepository.searchTrackInNetwork(query)

                // The tracks have been successfully found. Update the ui state
                _uiState.update {
                    it.copy(loadingState = UiState.LoadingState.IDLE, loadedTracks = tracks)
                }
            } catch (ex: CancellationException) {
                throw ex // We do not want to handle CancellationException
            } catch (ex: Exception) {
                // Catch all exceptions here

                // Report about the found exception to logs
                when (ex) {
                    is IOException -> {
                        Log.e(TAG, "IOException", ex)
                    }

                    is HttpException -> {
                        Log.e(TAG, "HTTPException | message = ${ex.message()}", ex)
                    }

                    else -> Log.e(TAG, "UnknownException: $ex", ex)
                }

                // Change the loading state of the ui state
                _uiState.update { it.copy(loadingState = UiState.LoadingState.ERROR) }
            }
        }
    }

    fun downloadTrack(track: Track) {
        viewModelScope.launch {
            tracksRepository.downloadTrack(track)
        }
    }
}