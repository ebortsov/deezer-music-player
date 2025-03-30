package com.github.ebortsov.deezermusicplayer.screens.apitracks.adapter

import androidx.recyclerview.widget.DiffUtil
import com.github.ebortsov.deezermusicplayer.model.Track

class TrackDiffUtil : DiffUtil.ItemCallback<Track>() {
    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.id == newItem.id
    }
}