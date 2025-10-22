package me.cniekirk.ontrack.core.navigation

enum class StationType {
    TARGET,
    FILTER
}

data class StationResult(
    val stationType: StationType,
    val crs: String,
    val name: String
)