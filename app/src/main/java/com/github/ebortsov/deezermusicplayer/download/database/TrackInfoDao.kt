package com.github.ebortsov.deezermusicplayer.download.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.ebortsov.deezermusicplayer.model.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackInfoDao {
    @Query("SELECT track FROM track_info")
    fun getTracksAsFlow(): Flow<List<Track>>

    @Query("SELECT track FROM track_info")
    suspend fun getTracks(): List<Track>

    @Delete
    suspend fun removeTrack(trackInfo: TrackInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(trackInfo: TrackInfo)

    @Query("DELETE FROM track_info")
    suspend fun clearTable()

    @Query("SELECT track FROM track_info WHERE track_id = :trackId")
    suspend fun findTrack(trackId: Long): List<Track>
}