package me.cniekirk.ontrack.core.database.di

import android.content.Context
import androidx.room.Room
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import me.cniekirk.ontrack.core.database.dao.StationDao
import me.cniekirk.ontrack.core.database.database.StationDatabase
import me.cniekirk.ontrack.core.di.components.ApplicationContext

private const val DB_NAME = "station_db"

@ContributesTo(AppScope::class)
interface DatabaseProviders {

    @Provides
    @SingleIn(AppScope::class)
    fun provideStationDatabase(@ApplicationContext context: Context): StationDatabase {
        return Room.databaseBuilder(
            context = context,
            StationDatabase::class.java,
            DB_NAME
        ).build()
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideStationDao(stationDatabase: StationDatabase): StationDao = stationDatabase.stationDao()
}