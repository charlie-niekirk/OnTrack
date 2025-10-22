package me.cniekirk.ontrack.core.domain.model.services

sealed interface DepartureStatus {

    data class Departed(
        val actualDepartureTime: String,
        val estimatedDepartureTime: String
    ) : DepartureStatus

    data class Upcoming(
        val scheduledDepartureTime: String,
        val estimatedDepartureTime: String
    ) : DepartureStatus

    data object Delayed : DepartureStatus

    data object Cancelled : DepartureStatus
}
