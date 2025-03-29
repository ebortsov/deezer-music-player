package com.github.ebortsov.deezermusicplayer.screens.apitracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.ebortsov.deezermusicplayer.databinding.FragmentApiTracksBinding
import com.github.ebortsov.deezermusicplayer.R

class ApiTracksFragment : Fragment() {
    private lateinit var binding: FragmentApiTracksBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentApiTracksBinding.inflate(inflater, container, false)
        return binding.root
    }
}
