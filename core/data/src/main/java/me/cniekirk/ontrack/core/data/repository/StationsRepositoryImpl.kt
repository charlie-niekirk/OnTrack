package me.cniekirk.ontrack.core.data.repository

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import me.cniekirk.ontrack.core.data.mapper.toStation
import me.cniekirk.ontrack.core.database.dao.StationDao
import me.cniekirk.ontrack.core.database.entity.StationEntity
import me.cniekirk.ontrack.core.domain.model.Station
import me.cniekirk.ontrack.core.domain.repository.StationsRepository
import me.cniekirk.ontrack.core.network.api.openraildata.OpenRailDataApi

@ContributesBinding(AppScope::class)
@Inject
internal class StationsRepositoryImpl(
    private val openRailDataApi: OpenRailDataApi,
    private val stationDao: StationDao
) : StationsRepository {

    override suspend fun updateStations() {
        val corpusEntries = openRailDataApi.getCorpus()

        // Filter to train stations (those with CRS codes) and map to entities
        val stations = corpusEntries
            .filter { entry ->
                val crs = entry.crs
                crs != null && crs.isNotEmpty() && entry.tiploc != null && entry.name != null
            }
            .map { StationEntity(tiploc = it.tiploc!!, name = it.name!!, crs = it.crs!!) }

        stationDao.insertAll(stations)
    }

    override suspend fun getStations(forceRefresh: Boolean): List<Station> {
        val isEmpty = stationDao.getCount() == 0

        if (forceRefresh || isEmpty) {
            updateStations()
        }

        return stationDao.getAllStations().map { it.toStation() }
    }
}