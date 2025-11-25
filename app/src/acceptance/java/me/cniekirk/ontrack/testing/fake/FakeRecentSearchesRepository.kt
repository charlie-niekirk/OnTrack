package me.cniekirk.ontrack.testing.fake

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.cniekirk.ontrack.core.domain.model.arguments.RequestTime
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceListRequest
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceListType
import me.cniekirk.ontrack.core.domain.model.arguments.TrainStation
import me.cniekirk.ontrack.core.domain.model.error.LocalDataError
import me.cniekirk.ontrack.core.domain.repository.RecentSearchesRepository

class FakeRecentSearchesRepository : RecentSearchesRepository {

    private val recentSearches = mutableListOf<ServiceListRequest>().apply {
        addAll(
            listOf(
                ServiceListRequest(
                    serviceListType = ServiceListType.DEPARTURES,
                    requestTime = RequestTime.Now,
                    targetStation = TrainStation(crs = "PAD", name = "London Paddington"),
                    filterStation = null
                ),
                ServiceListRequest(
                    serviceListType = ServiceListType.DEPARTURES,
                    requestTime = RequestTime.Now,
                    targetStation = TrainStation(crs = "RDG", name = "Reading"),
                    filterStation = TrainStation(crs = "PAD", name = "London Paddington")
                ),
                ServiceListRequest(
                    serviceListType = ServiceListType.ARRIVALS,
                    requestTime = RequestTime.Now,
                    targetStation = TrainStation(crs = "OXF", name = "Oxford"),
                    filterStation = null
                )
            )
        )
    }

    override suspend fun cacheRecentSearch(serviceListRequest: ServiceListRequest): Result<Unit, LocalDataError> {
        recentSearches.remove(serviceListRequest)
        recentSearches.add(0, serviceListRequest)
        return Ok(Unit)
    }

    override suspend fun getRecentSearches(): Flow<List<ServiceListRequest>> = flowOf(recentSearches)


    override suspend fun deleteRecentSearch(serviceListRequest: ServiceListRequest): Result<Unit, LocalDataError> {
        recentSearches.remove(serviceListRequest)
        return Ok(Unit)
    }

    override suspend fun deleteAllRecentSearches(): Result<Unit, LocalDataError> {
        recentSearches.clear()
        return Ok(Unit)
    }
}
