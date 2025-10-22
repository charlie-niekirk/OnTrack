package me.cniekirk.ontrack.feature.stationsearch

import me.cniekirk.ontrack.core.domain.model.Station
import me.cniekirk.ontrack.core.navigation.StationType

data class StationSearchState(
    val stationType: StationType,
    val isLoading: Boolean = true,
    val stations: List<Station> = emptyList(),
    val searchQuery: String = ""
)