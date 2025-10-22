package me.cniekirk.ontrack.core.data.repository

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.map
import me.cniekirk.ontrack.core.data.mapper.toStation
import me.cniekirk.ontrack.core.data.util.safeApiCall
import me.cniekirk.ontrack.core.database.dao.StationDao
import me.cniekirk.ontrack.core.database.entity.StationEntity
import me.cniekirk.ontrack.core.domain.model.Station
import me.cniekirk.ontrack.core.domain.model.error.NetworkError
import me.cniekirk.ontrack.core.domain.repository.StationsRepository
import me.cniekirk.ontrack.core.network.api.openraildata.OpenRailDataApi

internal class StationsRepositoryImpl(
    private val openRailDataApi: OpenRailDataApi,
    private val stationDao: StationDao
) : StationsRepository {

    override suspend fun updateStations(): Result<Unit, NetworkError> {
        return safeApiCall { openRailDataApi.getStationList() }
            .map { stationListResponse ->
                // Filter to train stations (those with CRS codes) and map to entities
                val stations = stationListResponse.stationList
                    .map { StationEntity(it.stationCode, it.stationName) }

                stationDao.insertAll(stations)
            }
    }

    override suspend fun getStations(forceRefresh: Boolean): Result<List<Station>, NetworkError> {
        val isEmpty = stationDao.getCount() == 0

        return if (forceRefresh || isEmpty) {
            updateStations().andThen {
                Ok(stationDao.getAllStations().map { it.toStation() })
            }
        } else {
            Ok(stationDao.getAllStations().map { it.toStation() })
        }
    }
}