package me.cniekirk.ontrack.core.network.model.realtimetrains

import kotlinx.serialization.Serializable

@Serializable
data class Filter(
    val destination: Location
)