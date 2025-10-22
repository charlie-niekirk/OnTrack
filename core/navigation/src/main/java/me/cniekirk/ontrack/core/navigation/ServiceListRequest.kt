package me.cniekirk.ontrack.core.domain.model

enum class ServiceListType {
    DEPARTURES,
    ARRIVALS
}


data class ServiceListRequest(
    val serviceListType: ServiceListType,
    val targetStation: Station,
    val filterStation: Station?
)
