package com.github.ebortsov.deezermusicplayer.download.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

private const val TRACK_INFO_DATABASE_NAME = "track_info_database"

@Database(
    entities = [TrackInfo::class],
    version = 1
)
@TypeConverters(TrackInfoConverters::class)
abstract class TrackInfoDatabase : RoomDatabase() {
    abstract fun trackInfoDao(): TrackInfoDao

    companion object {
        private var instance: TrackInfoDatabase? = null

        fun initialize(context: Context) {
            instance = Room.databaseBuilder(
                context,
                TrackInfoDatabase::class.java,
                TRACK_INFO_DATABASE_NAME
            ).build()
        }

        fun getInstance(): TrackInfoDatabase {
            return checkNotNull(instance) { "TrackInfoDatabase must be initialized" }
        }
    }
}