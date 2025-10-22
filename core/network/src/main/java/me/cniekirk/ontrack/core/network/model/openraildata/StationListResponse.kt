package me.cniekirk.ontrack.core.network.model.openraildata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StationListResponse(
    @SerialName("version")
    val version: String,
    @SerialName("StationList")
    val stationList: List<StationEntry>
)