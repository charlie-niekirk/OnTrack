package me.cniekirk.ontrack.core.network.model.realtimetrains.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ServiceType {
    @SerialName("bus") BUS,
    @SerialName("ship") SHIP,
    @SerialName("train") TRAIN
}