package com.github.ebortsov.deezermusicplayer.download.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.ebortsov.deezermusicplayer.model.Track

@Entity(tableName = "track_info")
data class TrackInfo(
    @ColumnInfo(name = "track_id")
    @PrimaryKey
    val trackId: Long,

    val track: Track
)