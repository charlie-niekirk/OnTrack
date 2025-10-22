package me.cniekirk.ontrack.feature.home.state

import me.cniekirk.ontrack.core.navigation.ServiceListRequest

sealed interface HomeEffect {

    data class NavigateToServiceList(
        val serviceListRequest: ServiceListRequest
    ) : HomeEffect
}