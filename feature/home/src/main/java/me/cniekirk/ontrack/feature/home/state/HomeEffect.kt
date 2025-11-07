package me.cniekirk.ontrack.feature.home.state

import me.cniekirk.ontrack.core.domain.model.arguments.ServiceListRequest

sealed interface HomeEffect {

    data class NavigateToServiceList(
        val serviceListRequest: ServiceListRequest
    ) : HomeEffect

    data object ShowNoStationSelectedError : HomeEffect

    data object ShowFailedToFetchRecentSearchesError : HomeEffect
}