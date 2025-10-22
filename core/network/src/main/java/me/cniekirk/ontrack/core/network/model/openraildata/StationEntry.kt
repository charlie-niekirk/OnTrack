package me.cniekirk.ontrack.core.network.model.openraildata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StationEntry(
    @SerialName("crs")
    val stationCode: String,
    @SerialName("Value")
    val stationName: String
)
