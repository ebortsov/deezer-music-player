package com.github.ebortsov.deezermusicplayer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.createGraph
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.github.ebortsov.deezermusicplayer.screens.apitracks.ApiTracksDestination
import com.github.ebortsov.deezermusicplayer.screens.apitracks.apiTracksDestination
import com.github.ebortsov.deezermusicplayer.screens.playback.playbackDestination

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Edge to edge setup
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Navigation setup
        val navHostFragment = checkNotNull(supportFragmentManager.findFragmentById(R.id.nav_host_fragment))
        val navController = navHostFragment.findNavController()
        navController.graph = navController.createGraph(
            startDestination = ApiTracksDestination
        ) {
            apiTracksDestination()
            playbackDestination()
        }
    }
}