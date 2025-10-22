package me.cniekirk.ontrack.core.domain.model.services

sealed interface TimeStatus {

    data class Departed(
        val actualDepartureTime: String,
        val scheduledDepartureTime: String,
        val delayInMinutes: Int
    ) : TimeStatus

    data class Arrived(
        val actualArrivalTime: String,
        val scheduledArrivalTime: String,
        val delayInMinutes: Int
    ) : TimeStatus

    data class OnTime(
        val scheduledTime: String
    ) : TimeStatus

    data class Delayed(
        val scheduledTime: String,
        val estimatedTime: String,
        val delayInMinutes: Int
    ) : TimeStatus

    data class Cancelled(
        val scheduledDepartureTime: String,
        val reason: String
    ) : TimeStatus

    data object Unknown : TimeStatus
}
