package me.cniekirk.ontrack.feature.servicelist

import me.cniekirk.ontrack.feature.servicelist.model.ServiceListError

sealed interface ServiceListEffect {

    data class DisplayError(
        val serviceListError: ServiceListError
    ) : ServiceListEffect
}