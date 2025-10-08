package me.cniekirk.ontrack.core.network.model.realtimetrains

import kotlinx.serialization.Serializable

@Serializable
data class Association(
    val type: String,
    val associatedUid: String,
    val associatedRunDate: String
)