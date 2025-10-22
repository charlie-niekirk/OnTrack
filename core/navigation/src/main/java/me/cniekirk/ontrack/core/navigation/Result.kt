package me.cniekirk.ontrack.navigation

import kotlinx.serialization.Serializable

@Serializable
data class StationResult(
    val crs: String,
    val name: String
)

@Serializable
data class StationSelectionResult(
    val stationResult: StationResult
)