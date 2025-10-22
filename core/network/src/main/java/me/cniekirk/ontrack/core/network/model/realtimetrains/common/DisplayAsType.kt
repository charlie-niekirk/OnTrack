package me.cniekirk.ontrack.core.network.model.realtimetrains.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DisplayAsType {
    @SerialName("CALL") CALL,
    @SerialName("PASS") PASS,
    @SerialName("ORIGIN") ORIGIN,
    @SerialName("DESTINATION") DESTINATION,
    @SerialName("STARTS") STARTS,
    @SerialName("TERMINATES") TERMINATES,
    @SerialName("CANCELLED_CALL") CANCELLED_CALL,
    @SerialName("CANCELLED_PASS") CANCELLED_PASS
}