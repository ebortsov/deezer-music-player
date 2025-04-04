package com.github.ebortsov.deezermusicplayer.screens.apitracks.adapter

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.ebortsov.deezermusicplayer.databinding.TrackListItemBinding
import com.github.ebortsov.deezermusicplayer.model.Track
import com.github.ebortsov.deezermusicplayer.R

fun interface OnTrackClickListener {
    fun onClick(view: View, track: Track)
}

class TrackViewHolder(
    private val binding: TrackListItemBinding,
    private val onTrackClickListener: OnTrackClickListener
) : RecyclerView.ViewHolder(binding.root) {
    fun onBind(track: Track) {
        binding.trackTitleTextView.text = track.title
        binding.authorNameTextView.text = track.artist.name

        // Load the image
        Glide.with(binding.root.context)
            .load(track.album.coverUri.toString())
            .placeholder(R.drawable.ic_image)
            .into(binding.trackCoverImageView)

        binding.root.setOnClickListener { v -> onTrackClickListener.onClick(v, track) }
    }
}