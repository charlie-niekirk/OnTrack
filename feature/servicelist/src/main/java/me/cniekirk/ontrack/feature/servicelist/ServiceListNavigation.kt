package me.cniekirk.ontrack.feature.servicelist

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import me.cniekirk.ontrack.core.navigation.ServiceListRequest

@Serializable
data class ServiceList(
    val serviceListRequest: ServiceListRequest
) : NavKey