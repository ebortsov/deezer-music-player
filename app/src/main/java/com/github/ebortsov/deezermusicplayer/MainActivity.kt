package com.github.ebortsov.deezermusicplayer

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.createGraph
import androidx.navigation.fragment.findNavController
import com.github.ebortsov.deezermusicplayer.databinding.ActivityMainBinding
import com.github.ebortsov.deezermusicplayer.screens.tracklist.apitracks.ApiTracksDestination
import com.github.ebortsov.deezermusicplayer.screens.tracklist.apitracks.apiTracksDestination
import com.github.ebortsov.deezermusicplayer.screens.playback.playbackDestination
import com.github.ebortsov.deezermusicplayer.screens.tracklist.savedtracks.SavedTracksDestination
import com.github.ebortsov.deezermusicplayer.screens.tracklist.savedtracks.savedTracksDestination

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Edge to edge setup
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Navigation setup
        val navHostFragment =
            checkNotNull(supportFragmentManager.findFragmentById(R.id.nav_host_fragment))
        val navController = navHostFragment.findNavController()
        navController.graph = navController.createGraph(
            startDestination = ApiTracksDestination
        ) {
            apiTracksDestination()
            savedTracksDestination()
            playbackDestination()
        }
        binding.openSavedTracksButton.setOnClickListener {
            navController.navigate(route = SavedTracksDestination) {
                launchSingleTop = true

                popUpTo(SavedTracksDestination) { inclusive = true }
            }
        }
        binding.openApiTracksButton.setOnClickListener {
            navController.navigate(route = ApiTracksDestination) {
                launchSingleTop = true

                popUpTo(ApiTracksDestination) { inclusive = true }
            }
        }
    }
}