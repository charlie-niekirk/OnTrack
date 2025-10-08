package me.cniekirk.ontrack.feature.stationsearch

import me.cniekirk.ontrack.core.domain.model.Station

internal data class StationSearchState(
    val isLoading: Boolean = true,
    val stations: List<Station> = emptyList()
)