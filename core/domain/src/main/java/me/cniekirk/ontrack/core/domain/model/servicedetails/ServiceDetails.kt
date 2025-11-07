package me.cniekirk.ontrack.core.domain.model.servicedetails

data class ServiceDetails(
    val trainOperatingCompany: String,
    val origin: String,
    val destination: String,
    val locations: List<Location>
)
