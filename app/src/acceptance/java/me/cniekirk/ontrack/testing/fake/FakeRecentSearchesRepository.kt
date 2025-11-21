package me.cniekirk.ontrack.testing.fake

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import me.cniekirk.ontrack.core.domain.model.arguments.RequestTime
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceListRequest
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceListType
import me.cniekirk.ontrack.core.domain.model.arguments.TrainStation
import me.cniekirk.ontrack.core.domain.model.error.LocalDataError
import me.cniekirk.ontrack.core.domain.repository.RecentSearchesRepository

class FakeRecentSearchesRepository : RecentSearchesRepository {

    private val recentSearches = mutableListOf<ServiceListRequest>()

    init {
        // Pre-populate with some fake recent searches
        recentSearches.addAll(
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
        // Add to the beginning of the list
        recentSearches.remove(serviceListRequest)
        recentSearches.add(0, serviceListRequest)
        return Ok(Unit)
    }

    override suspend fun getRecentSearches(): Result<List<ServiceListRequest>, LocalDataError> {
        return Ok(recentSearches.toList())
    }

    override suspend fun deleteRecentSearch(serviceListRequest: ServiceListRequest): Result<Unit, LocalDataError> {
        recentSearches.remove(serviceListRequest)
        return Ok(Unit)
    }

    override suspend fun deleteAllRecentSearches(): Result<Unit, LocalDataError> {
        recentSearches.clear()
        return Ok(Unit)
    }
}
