package me.cniekirk.ontrack.core.domain.model.servicedetails

import me.cniekirk.ontrack.core.domain.model.services.Platform
import me.cniekirk.ontrack.core.domain.model.services.ServiceLocation
import me.cniekirk.ontrack.core.domain.model.services.TimeStatus

data class Location(
    val locationName: String,
    val departureTimeStatus: TimeStatus,
    val arrivalTimeStatus: TimeStatus,
    val platform: Platform,
    val serviceLocation: ServiceLocation?
)
