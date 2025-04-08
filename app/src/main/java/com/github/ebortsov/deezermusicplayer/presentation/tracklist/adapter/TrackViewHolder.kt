package com.github.ebortsov.deezermusicplayer.presentation.tracklist.adapter

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.ebortsov.deezermusicplayer.databinding.TrackListItemBinding
import com.github.ebortsov.deezermusicplayer.model.Track
import com.github.ebortsov.deezermusicplayer.R

fun interface OnTrackClickListener {
    fun onClick(view: View, track: Track)
}

fun interface OnTrackDownloadListener {
    fun onDownload(view: View, track: Track)
}

class TrackViewHolder(
    private val binding: TrackListItemBinding,
    private val onTrackClickListener: OnTrackClickListener,
    private val onTrackDownloadListener: OnTrackDownloadListener
) : RecyclerView.ViewHolder(binding.root) {
    fun onBind(track: Track) {
        binding.trackTitleTextView.text = track.title
        binding.authorNameTextView.text = track.artist.name

        // If the track is remote (i.e. scheme starts with http*,
        // then show the download button
        val trackIsRemote = track.previewUri.scheme.startsWith("http")
        binding.downloadTrackButton.isVisible = trackIsRemote
        binding.downloadTrackButton.setOnClickListener { v ->
            if (trackIsRemote) {
                onTrackDownloadListener.onDownload(v, track)
            }
        }

        Glide.with(binding.root.context)
            .load(track.album.coverUri.toString())
            .placeholder(R.drawable.ic_image)
            .into(binding.trackCoverImageView)

        binding.root.setOnClickListener { v -> onTrackClickListener.onClick(v, track) }
    }
}