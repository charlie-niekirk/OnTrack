package me.cniekirk.ontrack.core.network.model.realtimetrains.servicelist

import kotlinx.serialization.Serializable

@Serializable
data class LocationDetail(
    val name: String?,
    val crs: String?,
)