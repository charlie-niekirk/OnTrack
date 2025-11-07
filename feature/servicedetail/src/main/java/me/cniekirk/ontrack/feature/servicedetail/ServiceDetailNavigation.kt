package me.cniekirk.ontrack.feature.servicedetail

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceDetailRequest

@Serializable
data class ServiceDetail(
    val serviceDetailRequest: ServiceDetailRequest
) : NavKey