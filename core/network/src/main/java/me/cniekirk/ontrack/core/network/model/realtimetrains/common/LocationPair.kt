package me.cniekirk.ontrack.core.network.model.realtimetrains.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationPair(
    @SerialName("tiploc") val tiploc: String? = null,
    @SerialName("description") val description: String,
    @SerialName("workingTime") val workingTime: String? = null,
    @SerialName("publicTime") val publicTime: String
)