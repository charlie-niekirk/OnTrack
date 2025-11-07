package me.cniekirk.ontrack.feature.home.state

import me.cniekirk.ontrack.core.domain.model.Station
import me.cniekirk.ontrack.core.domain.model.arguments.RequestTime
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceListRequest

sealed interface StationSelection {

    data object None : StationSelection

    data class Selected(val station: Station) : StationSelection
}

enum class QueryType {
    DEPARTURES,
    ARRIVALS
}

data class HomeState(
    val queryType: QueryType = QueryType.DEPARTURES,
    val targetStationSelection: StationSelection = StationSelection.None,
    val filterStationSelection: StationSelection = StationSelection.None,
    val requestTime: RequestTime = RequestTime.Now,
    val currentDateMillis: Long,
    val recentSearches: List<ServiceListRequest> = emptyList()
)