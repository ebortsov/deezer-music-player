package com.github.ebortsov.deezermusicplayer.download.database

import androidx.room.TypeConverter
import com.github.ebortsov.deezermusicplayer.model.Track
import kotlinx.serialization.json.Json

class TrackInfoConverters {
    @TypeConverter
    fun trackToString(value: Track): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun stringToTrack(value: String): Track {
        return Json.decodeFromString<Track>(value)
    }
}