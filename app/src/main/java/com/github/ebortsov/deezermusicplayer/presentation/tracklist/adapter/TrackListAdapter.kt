package com.github.ebortsov.deezermusicplayer.presentation.tracklist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.github.ebortsov.deezermusicplayer.databinding.TrackListItemBinding
import com.github.ebortsov.deezermusicplayer.model.Track

class TrackListAdapter(
    private val onTrackClickListener: OnTrackClickListener,
    private val onTrackDownloadListener: OnTrackDownloadListener
) : ListAdapter<Track, TrackViewHolder>(TrackDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TrackListItemBinding.inflate(inflater, parent, false)
        return TrackViewHolder(binding, onTrackClickListener, onTrackDownloadListener)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = currentList[position]
        holder.onBind(track)
    }
}