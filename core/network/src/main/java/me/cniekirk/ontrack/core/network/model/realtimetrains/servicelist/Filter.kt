package me.cniekirk.ontrack.core.network.model.realtimetrains.servicelist

import kotlinx.serialization.Serializable
import me.cniekirk.ontrack.core.network.model.realtimetrains.common.ServiceStopLocation

@Serializable
data class Filter(
    val from: ServiceStopLocation? = null,
    val to: ServiceStopLocation? = null
)