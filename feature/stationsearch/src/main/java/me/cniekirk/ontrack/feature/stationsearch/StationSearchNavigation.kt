package me.cniekirk.ontrack.feature.stationsearch

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import me.cniekirk.ontrack.core.navigation.StationType

@Serializable
data class StationSearch(
    val stationType: StationType
) : NavKey