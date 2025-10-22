package me.cniekirk.ontrack.core.navigation

import kotlinx.serialization.Serializable
import me.cniekirk.ontrack.core.domain.model.Station

enum class ServiceListType {
    DEPARTURES,
    ARRIVALS
}

@Serializable
sealed interface RequestTime {

    @Serializable
    data object Now : RequestTime

    @Serializable
    data class AtTime(
        val year: String,
        val month: String,
        val day: String,
        val hours: String,
        val mins: String
    ) : RequestTime
}

@Serializable
data class TrainStation(
    val crs: String,
    val name: String
)

@Serializable
data class ServiceListRequest(
    val serviceListType: ServiceListType,
    val requestTime: RequestTime,
    val targetStation: TrainStation,
    val filterStation: TrainStation?
)
