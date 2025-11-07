package me.cniekirk.ontrack.feature.servicedetail

import me.cniekirk.ontrack.core.domain.model.servicedetails.ServiceDetails

data class ServiceDetailState(
    val isLoading: Boolean = true,
    val serviceDetails: ServiceDetails? = null
)