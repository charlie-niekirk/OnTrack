package me.cniekirk.ontrack.core.domain.model.services

data class TrainService(
    val serviceId: String,
    val origin: String,
    val destination: String,
    val timeStatus: TimeStatus,
    val platform: Platform?,
    val serviceLocation: ServiceLocation?,
    val trainOperatingCompany: String
)