package me.cniekirk.ontrack.core.data.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import me.cniekirk.ontrack.core.data.mapper.ServiceMapper
import me.cniekirk.ontrack.core.data.repository.RealtimeTrainsRepositoryImpl
import me.cniekirk.ontrack.core.data.repository.RecentSearchesRepositoryImpl
import me.cniekirk.ontrack.core.data.repository.StationsRepositoryImpl
import me.cniekirk.ontrack.core.data.work.UpdateStationsWorker
import me.cniekirk.ontrack.core.database.dao.StationDao
import me.cniekirk.ontrack.core.datastore.RecentSearchesDataSource
import me.cniekirk.ontrack.core.domain.repository.RealtimeTrainsRepository
import me.cniekirk.ontrack.core.domain.repository.RecentSearchesRepository
import me.cniekirk.ontrack.core.domain.repository.StationsRepository
import me.cniekirk.ontrack.core.network.api.openraildata.OpenRailDataApi
import me.cniekirk.ontrack.core.network.api.realtimetrains.RealtimeTrainsApi
import me.cniekirk.ontrack.core.platform.TimeProvider

@ContributesTo(AppScope::class)
interface DataProviders {

    val updateStationsWorkerFactory: UpdateStationsWorker.Factory

    @SingleIn(AppScope::class)
    @Provides
    fun provideServiceMapper(
        timeProvider: TimeProvider
    ): ServiceMapper {
        return ServiceMapper(timeProvider)
    }

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
        realtimeTrainsApi: RealtimeTrainsApi,
        serviceMapper: ServiceMapper
    ): RealtimeTrainsRepository {
        return RealtimeTrainsRepositoryImpl(realtimeTrainsApi, serviceMapper)
    }

    @SingleIn(AppScope::class)
    @Provides
    fun provideRecentSearchesRepository(
        recentSearchesDataSource: RecentSearchesDataSource
    ): RecentSearchesRepository {
        return RecentSearchesRepositoryImpl(recentSearchesDataSource)
    }
}