package me.cniekirk.ontrack.feature.servicedetail

import me.cniekirk.ontrack.feature.servicedetail.model.ServiceDetailError

sealed interface ServiceDetailEffect {

    data class ShowError(val serviceDetailsError: ServiceDetailError) : ServiceDetailEffect
}