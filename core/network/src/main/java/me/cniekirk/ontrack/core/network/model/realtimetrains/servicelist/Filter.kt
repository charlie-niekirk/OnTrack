package me.cniekirk.ontrack.core.network.model.realtimetrains.servicelist

import kotlinx.serialization.Serializable
import me.cniekirk.ontrack.core.network.model.realtimetrains.common.ServiceLocation

@Serializable
data class Filter(
    val from: ServiceLocation? = null,
    val to: ServiceLocation? = null
)