package me.cniekirk.ontrack.core.network.model.realtimetrains

import kotlinx.serialization.Serializable

@Serializable
data class Service(
    val locationDetail: LocationDetail,
    val serviceUid: String,
    val runDate: String,
    val trainIdentity: String,
    val runningIdentity: String,
    val atocCode: String,
    val atocName: String,
    val serviceType: String,
    val isPassenger: Boolean
)