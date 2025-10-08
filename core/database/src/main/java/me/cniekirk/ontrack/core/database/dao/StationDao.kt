package me.cniekirk.ontrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.cniekirk.ontrack.core.database.entity.StationEntity

@Dao
interface StationDao {

    @Query("SELECT COUNT(*) FROM stations")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stationEntities: List<StationEntity>)

    // Example query for later use
    @Query("SELECT * FROM stations")
    suspend fun getAllStations(): List<StationEntity>
}