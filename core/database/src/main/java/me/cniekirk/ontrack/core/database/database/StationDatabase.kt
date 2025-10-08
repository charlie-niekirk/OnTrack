package me.cniekirk.ontrack.core.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import me.cniekirk.ontrack.core.database.dao.StationDao
import me.cniekirk.ontrack.core.database.entity.StationEntity

@Database(entities = [StationEntity::class], version = 1, exportSchema = false)
abstract class StationDatabase : RoomDatabase() {
    abstract fun stationDao(): StationDao
}