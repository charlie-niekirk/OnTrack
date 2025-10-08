package me.cniekirk.ontrack.core.network.model.realtimetrains

import kotlinx.serialization.Serializable

@Serializable
data class OriginDestination(
    val tiploc: String,
    val description: String,
    val workingTime: String,
    val publicTime: String
)