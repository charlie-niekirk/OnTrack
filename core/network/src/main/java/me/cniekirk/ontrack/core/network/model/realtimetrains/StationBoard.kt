package me.cniekirk.ontrack.core.network.model.realtimetrains

import kotlinx.serialization.Serializable

@Serializable
data class StationBoard(
    val location: Location,
    val filter: Filter? = null,
    val services: List<Service>
)