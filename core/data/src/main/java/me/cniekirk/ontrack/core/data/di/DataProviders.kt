package me.cniekirk.ontrack.core.data.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import me.cniekirk.ontrack.core.data.repository.RealtimeTrainsRepositoryImpl
import me.cniekirk.ontrack.core.data.repository.StationsRepositoryImpl
import me.cniekirk.ontrack.core.data.work.UpdateStationsWorker
import me.cniekirk.ontrack.core.database.dao.StationDao
import me.cniekirk.ontrack.core.domain.repository.RealtimeTrainsRepository
import me.cniekirk.ontrack.core.domain.repository.StationsRepository
import me.cniekirk.ontrack.core.network.api.openraildata.OpenRailDataApi
import me.cniekirk.ontrack.core.network.api.realtimetrains.RealtimeTrainsApi

@ContributesTo(AppScope::class)
interface DataProviders {

    val updateStationsWorkerFactory: UpdateStationsWorker.Factory

    @SingleIn(AppScope::class)
    @Provides
    fun provideStationsRepository(
        openRailDataApi: OpenRailDataApi,
        stationDao: StationDao
    ): StationsRepository {
        return StationsRepositoryImpl(openRailDataApi, stationDao)
    }

    @SingleIn(AppScope::class)
    @Provides
    fun provideRealtimeTrainsRepository(
        realtimeTrainsApi: RealtimeTrainsApi
    ): RealtimeTrainsRepository {
        return RealtimeTrainsRepositoryImpl(realtimeTrainsApi)
    }
}