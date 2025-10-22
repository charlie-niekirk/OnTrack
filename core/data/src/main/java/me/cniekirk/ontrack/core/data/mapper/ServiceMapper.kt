package me.cniekirk.ontrack.core.data.mapper

import me.cniekirk.ontrack.core.domain.model.services.Platform
import me.cniekirk.ontrack.core.domain.model.services.ServiceLocation
import me.cniekirk.ontrack.core.domain.model.services.TimeStatus
import me.cniekirk.ontrack.core.domain.model.services.TrainService
import me.cniekirk.ontrack.core.network.model.realtimetrains.common.ServiceLocationType
import me.cniekirk.ontrack.core.network.model.realtimetrains.servicelist.BoardService
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.min

fun BoardService.toTrainService(isArrival: Boolean = false): TrainService {
    return TrainService(
        serviceId = this.serviceUid,
        origin = this.locationDetail.origin.first().description,
        destination = this.locationDetail.destination.last().description,
        timeStatus = if (isArrival) this.calculateArrivalTimeStatus() else this.calculateDepartureTimeStatus(),
        platform = this.getPlatform(),
        serviceLocation = this.getServiceLocation(),
        trainOperatingCompany = this.atocName
    )
}

fun BoardService.getServiceLocation(): ServiceLocation? {
    return when (this.locationDetail.serviceLocation) {
        ServiceLocationType.APPR_STAT -> ServiceLocation.APPROACHING_STATION
        ServiceLocationType.APPR_PLAT -> ServiceLocation.APPROACHING_PLATFORM
        ServiceLocationType.AT_PLAT -> ServiceLocation.AT_PLATFORM
        ServiceLocationType.DEP_PREP -> ServiceLocation.PREPARING_DEPARTURE
        ServiceLocationType.DEP_READY -> ServiceLocation.READY_TO_DEPART
        else -> null
    }
}

fun BoardService.getPlatform(): Platform? {
    val platformName = this.locationDetail.platform ?: return null

    return if (this.locationDetail.platformConfirmed) {
        Platform.Confirmed(
            platformName = platformName,
            isChanged = this.locationDetail.platformChanged
        )
    } else {
        Platform.Estimated(
            platformName = platformName,
            isChanged = this.locationDetail.platformChanged
        )
    }
}

fun calculateTimeDifferenceInMinutes(time1: String, time2: String): Long {
    // Define the formatter for HHMM format
    val formatter = DateTimeFormatter.ofPattern("HHmm")

    // Parse the times into LocalTime objects
    val t1 = LocalTime.parse(time1, formatter)
    val t2 = LocalTime.parse(time2, formatter)

    // Calculate total minutes for each time
    val minutes1 = t1.hour * 60L + t1.minute
    val minutes2 = t2.hour * 60L + t2.minute

    // Calculate the absolute difference
    val diff = abs(minutes1 - minutes2)

    // Return the minimum difference considering midnight wrap-around (24-hour cycle)
    return min(diff, 1440L - diff)
}

fun BoardService.calculateDepartureTimeStatus(): TimeStatus {
    val scheduledDeparture = this.locationDetail.gbttBookedDeparture ?: return TimeStatus.Unknown

    if (this.locationDetail.isCancelled) {
        return TimeStatus.Cancelled(
            scheduledDepartureTime = scheduledDeparture,
            reason = this.locationDetail.cancelReasonLongText ?: "?"
        )
    } else {
        val realtimeDeparture = this.locationDetail.realtimeDeparture
        val isActual = this.locationDetail.realtimeDepartureActual

        if (isActual) {
            // If isActual is true then realtimeDeparture should not be null
            val actualDeparture = realtimeDeparture ?: return TimeStatus.Unknown
            return TimeStatus.Departed(
                actualDepartureTime = actualDeparture,
                scheduledDepartureTime = scheduledDeparture,
                delayInMinutes = calculateTimeDifferenceInMinutes(actualDeparture, scheduledDeparture).toInt()
            )
        } else {
            // In future, either on time or delayed
            return if (realtimeDeparture == null) {
                // No realtime so assume it's on time
                TimeStatus.OnTime(
                    scheduledTime = scheduledDeparture
                )
            } else {
                val timeDifference = calculateTimeDifferenceInMinutes(realtimeDeparture, scheduledDeparture).toInt()
                if (timeDifference > 0) {
                    TimeStatus.Delayed(
                        scheduledTime = scheduledDeparture,
                        estimatedTime = realtimeDeparture,
                        delayInMinutes = timeDifference
                    )
                } else {
                    TimeStatus.OnTime(
                        scheduledTime = scheduledDeparture
                    )
                }
            }
        }
    }
}

fun BoardService.calculateArrivalTimeStatus(): TimeStatus {
    val scheduledArrival = this.locationDetail.gbttBookedArrival ?: return TimeStatus.Unknown

    if (this.locationDetail.isCancelled) {
        return TimeStatus.Cancelled(
            scheduledDepartureTime = scheduledArrival,
            reason = this.locationDetail.cancelReasonLongText ?: "?"
        )
    } else {
        val realtimeArrival = this.locationDetail.realtimeArrival
        val isActual = this.locationDetail.realtimeArrivalActual

        if (isActual) {
            // If isActual is true then realtimeDeparture should not be null
            val actualArrival = realtimeArrival ?: return TimeStatus.Unknown
            return TimeStatus.Arrived(
                actualArrivalTime = actualArrival,
                scheduledArrivalTime = scheduledArrival,
                delayInMinutes = calculateTimeDifferenceInMinutes(actualArrival, scheduledArrival).toInt()
            )
        } else {
            // In future, either on time or delayed
            return if (realtimeArrival == null) {
                // No realtime so assume it's on time
                TimeStatus.OnTime(
                    scheduledTime = scheduledArrival
                )
            } else {
                val timeDifference = calculateTimeDifferenceInMinutes(realtimeArrival, scheduledArrival).toInt()
                if (timeDifference > 0) {
                    TimeStatus.Delayed(
                        scheduledTime = scheduledArrival,
                        estimatedTime = realtimeArrival,
                        delayInMinutes = timeDifference
                    )
                } else {
                    TimeStatus.OnTime(
                        scheduledTime = scheduledArrival
                    )
                }
            }
        }
    }
}