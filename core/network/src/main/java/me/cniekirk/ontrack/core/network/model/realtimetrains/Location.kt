package me.cniekirk.ontrack.core.network.model.realtimetrains

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val country: String,
    val crs: String,
    val name: String,
    val system: String,
    val tiploc: String
)