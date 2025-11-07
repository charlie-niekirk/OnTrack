package me.cniekirk.ontrack.core.data.mapper

import me.cniekirk.ontrack.core.domain.model.services.Platform
import me.cniekirk.ontrack.core.domain.model.services.RunDate
import me.cniekirk.ontrack.core.domain.model.services.ServiceLocation
import me.cniekirk.ontrack.core.domain.model.services.TimeStatus
import me.cniekirk.ontrack.core.domain.model.services.TrainService
import me.cniekirk.ontrack.core.network.model.realtimetrains.common.ServiceLocationType
import me.cniekirk.ontrack.core.network.model.realtimetrains.servicelist.BoardService
import me.cniekirk.ontrack.core.platform.TimeProvider
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.min

/**
 * Mapper class that converts network layer models to domain models.
 * Requires TimeProvider for date parsing functionality.
 */
class ServiceMapper(
    private val timeProvider: TimeProvider
) {

    /**
     * Maps a [BoardService] from the network layer to a [TrainService] domain model.
     *
     * @param boardService The board service to map
     * @param isArrival Whether to calculate arrival status (true) or departure status (false)
     * @return The mapped train service with all relevant information
     */
    fun toTrainService(boardService: BoardService, isArrival: Boolean = false): TrainService {
        val localDate = timeProvider.parseRunDate(boardService.runDate)

        return TrainService(
            serviceId = boardService.serviceUid,
            runDate = localDate.toRunDate(),
            origin = boardService.locationDetail.origin.first().description,
            destination = boardService.locationDetail.destination.last().description,
            timeStatus = if (isArrival) boardService.calculateArrivalTimeStatus() else boardService.calculateDepartureTimeStatus(),
            platform = boardService.getPlatform(),
            serviceLocation = boardService.getServiceLocation(),
            trainOperatingCompany = boardService.atocName
        )
    }

    /**
     * Converts a LocalDate to a RunDate domain model.
     */
    private fun LocalDate.toRunDate(): RunDate {
        return RunDate(
            day = dayOfMonth.toString().padStart(2, '0'),
            month = monthValue.toString().padStart(2, '0'),
            year = year.toString()
        )
    }
}

/**
 * Maps the service location type from network model to domain model.
 *
 * @return The mapped service location, or null if not available or unrecognized
 */
fun BoardService.getServiceLocation(): ServiceLocation? {
    return when (locationDetail.serviceLocation) {
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
 *
 * @return The platform information, or null if no platform is assigned
 */
fun BoardService.getPlatform(): Platform? {
    val platformName = locationDetail.platform ?: return null
    val isChanged = locationDetail.platformChanged

    return when {
        locationDetail.platformConfirmed -> Platform.Confirmed(platformName, isChanged)
        else -> Platform.Estimated(platformName, isChanged)
    }
}

/**
 * Calculates the time difference in minutes between two times in HHMM format.
 * Handles midnight wrap-around correctly (e.g., 2350 to 0010 = 20 minutes, not 1420 minutes).
 *
 * @param time1 First time in HHMM format (e.g., "1430")
 * @param time2 Second time in HHMM format (e.g., "1445")
 * @return The absolute time difference in minutes, accounting for midnight wrap-around
 */
internal fun calculateTimeDifferenceInMinutes(time1: String, time2: String): Long {
    val formatter = DateTimeFormatter.ofPattern("HHmm")

    val t1 = LocalTime.parse(time1, formatter)
    val t2 = LocalTime.parse(time2, formatter)

    val minutes1 = t1.hour * 60L + t1.minute
    val minutes2 = t2.hour * 60L + t2.minute

    val diff = abs(minutes1 - minutes2)

    // Return the minimum difference considering midnight wrap-around (24-hour cycle)
    return min(diff, 1440L - diff)
}

fun BoardService.calculateDepartureTimeStatus(): TimeStatus {
    return calculateTimeStatus(
        scheduledTime = locationDetail.gbttBookedDeparture,
        realtimeTime = locationDetail.realtimeDeparture,
        isActual = locationDetail.realtimeDepartureActual,
        isCancelled = locationDetail.isCancelled,
        cancelReason = locationDetail.cancelReasonLongText,
        isDeparture = true
    )
}

fun BoardService.calculateArrivalTimeStatus(): TimeStatus {
    return calculateTimeStatus(
        scheduledTime = locationDetail.gbttBookedArrival,
        realtimeTime = locationDetail.realtimeArrival,
        isActual = locationDetail.realtimeArrivalActual,
        isCancelled = locationDetail.isCancelled,
        cancelReason = locationDetail.cancelReasonLongText,
        isDeparture = false
    )
}

/**
 * Generic function to calculate time status for both departures and arrivals.
 * Consolidates common logic to reduce duplication.
 * Internal visibility allows reuse across mapper classes.
 */
internal fun calculateTimeStatus(
    scheduledTime: String?,
    realtimeTime: String?,
    isActual: Boolean,
    isCancelled: Boolean,
    cancelReason: String?,
    isDeparture: Boolean
): TimeStatus {
    val scheduled = scheduledTime ?: return TimeStatus.Unknown

    if (isCancelled) {
        return TimeStatus.Cancelled(
            scheduledDepartureTime = scheduled,
            reason = cancelReason ?: "?"
        )
    }

    return when {
        isActual -> createActualTimeStatus(
            actualTime = realtimeTime,
            scheduledTime = scheduled,
            isDeparture = isDeparture
        )
        else -> createScheduledTimeStatus(
            realtimeTime = realtimeTime,
            scheduledTime = scheduled
        )
    }
}

/**
 * Creates a time status for trains that have actually departed/arrived.
 * Internal visibility allows reuse across mapper classes.
 */
internal fun createActualTimeStatus(
    actualTime: String?,
    scheduledTime: String,
    isDeparture: Boolean
): TimeStatus {
    val actual = actualTime ?: return TimeStatus.Unknown
    val delay = calculateTimeDifferenceInMinutes(actual, scheduledTime).toInt()

    return if (isDeparture) {
        TimeStatus.Departed(
            actualDepartureTime = actual,
            scheduledDepartureTime = scheduledTime,
            delayInMinutes = delay
        )
    } else {
        TimeStatus.Arrived(
            actualArrivalTime = actual,
            scheduledArrivalTime = scheduledTime,
            delayInMinutes = delay
        )
    }
}

/**
 * Creates a time status for trains that are scheduled (not yet departed/arrived).
 * Returns OnTime if no realtime data available or no delay, otherwise Delayed.
 * Internal visibility allows reuse across mapper classes.
 */
internal fun createScheduledTimeStatus(
    realtimeTime: String?,
    scheduledTime: String
): TimeStatus {
    if (realtimeTime == null) {
        return TimeStatus.OnTime(scheduledTime = scheduledTime)
    }

    val timeDifference = calculateTimeDifferenceInMinutes(realtimeTime, scheduledTime).toInt()

    return if (timeDifference > 0) {
        TimeStatus.Delayed(
            scheduledTime = scheduledTime,
            estimatedTime = realtimeTime,
            delayInMinutes = timeDifference
        )
    } else {
        TimeStatus.OnTime(scheduledTime = scheduledTime)
    }
}