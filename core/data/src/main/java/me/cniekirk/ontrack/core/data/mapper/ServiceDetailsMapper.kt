package me.cniekirk.ontrack.core.data.mapper

import me.cniekirk.ontrack.core.domain.model.servicedetails.Location
import me.cniekirk.ontrack.core.domain.model.servicedetails.ServiceDetails
import me.cniekirk.ontrack.core.domain.model.services.Platform
import me.cniekirk.ontrack.core.domain.model.services.ServiceLocation
import me.cniekirk.ontrack.core.domain.model.services.TimeStatus
import me.cniekirk.ontrack.core.network.model.realtimetrains.common.ServiceStopLocation
import me.cniekirk.ontrack.core.network.model.realtimetrains.common.ServiceLocationType
import me.cniekirk.ontrack.core.network.model.realtimetrains.servicedetail.ServiceDetail

/**
 * Maps a [ServiceDetail] from the network layer to a [ServiceDetails] domain model.
 */
fun ServiceDetail.toServiceDetails(): ServiceDetails {
    return ServiceDetails(
        trainOperatingCompany = atocName,
        origin = origin.first().description,
        destination = destination.first().description,
        locations = locations.map { it.toLocation() }
    )
}

/**
 * Maps a [ServiceStopLocation] from the network layer to a [Location] domain model.
 */
fun ServiceStopLocation.toLocation(): Location {
    return Location(
        locationName = description,
        departureTimeStatus = calculateDepartureTimeStatus(),
        arrivalTimeStatus = calculateArrivalTimeStatus(),
        platform = getPlatform(),
        serviceLocation = getServiceLocation()
    )
}

/**
 * Maps the service location type from network model to domain model.
 */
private fun ServiceStopLocation.getServiceLocation(): ServiceLocation? {
    return when (serviceLocation) {
        ServiceLocationType.APPR_STAT -> ServiceLocation.APPROACHING_STATION
        ServiceLocationType.APPR_PLAT -> ServiceLocation.APPROACHING_PLATFORM
        ServiceLocationType.AT_PLAT -> ServiceLocation.AT_PLATFORM
        ServiceLocationType.DEP_PREP -> ServiceLocation.PREPARING_DEPARTURE
        ServiceLocationType.DEP_READY -> ServiceLocation.READY_TO_DEPART
        else -> null
    }
}

/**
 * Maps platform information from network model to domain model.
 * Distinguishes between confirmed and estimated platforms.
 */
private fun ServiceStopLocation.getPlatform(): Platform {
    val platformName = platform?.takeIf { it != "none" } ?: "?"
    val isChanged = platformChanged

    return when {
        platformConfirmed -> Platform.Confirmed(platformName, isChanged)
        else -> Platform.Estimated(platformName, isChanged)
    }
}

/**
 * Calculates departure time status for this location.
 * Delegates to shared logic in [calculateTimeStatus].
 */
private fun ServiceStopLocation.calculateDepartureTimeStatus(): TimeStatus {
    return calculateTimeStatus(
        scheduledTime = gbttBookedDeparture,
        realtimeTime = realtimeDeparture,
        isActual = realtimeDepartureActual,
        isCancelled = isCancelled,
        cancelReason = cancelReasonLongText,
        isDeparture = true
    )
}

/**
 * Calculates arrival time status for this location.
 * Delegates to shared logic in [calculateTimeStatus].
 */
private fun ServiceStopLocation.calculateArrivalTimeStatus(): TimeStatus {
    return calculateTimeStatus(
        scheduledTime = gbttBookedArrival,
        realtimeTime = realtimeArrival,
        isActual = realtimeArrivalActual,
        isCancelled = isCancelled,
        cancelReason = cancelReasonLongText,
        isDeparture = false
    )
}

