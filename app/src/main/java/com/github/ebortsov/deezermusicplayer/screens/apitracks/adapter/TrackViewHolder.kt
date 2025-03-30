package com.github.ebortsov.deezermusicplayer.screens.apitracks.adapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.ebortsov.deezermusicplayer.databinding.TrackListItemBinding
import com.github.ebortsov.deezermusicplayer.model.Track
import com.github.ebortsov.deezermusicplayer.R

class TrackViewHolder(
    private val binding: TrackListItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun onBind(track: Track) {
        binding.trackTitleTextView.text = track.title
        binding.authorNameTextView.text = track.artist.name

        // Load the image
        Glide.with(binding.root.context)
            .load(track.album.coverMediumUri.toString())
            .placeholder(R.drawable.ic_image)
            .into(binding.trackCoverImageView)
    }
}