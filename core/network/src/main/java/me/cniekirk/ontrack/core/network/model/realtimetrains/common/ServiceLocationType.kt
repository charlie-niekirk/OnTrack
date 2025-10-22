package me.cniekirk.ontrack.core.network.model.realtimetrains.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ServiceLocationType {
    @SerialName("APPR_STAT") APPR_STAT,
    @SerialName("APPR_PLAT") APPR_PLAT,
    @SerialName("AT_PLAT") AT_PLAT,
    @SerialName("DEP_PREP") DEP_PREP,
    @SerialName("DEP_READY") DEP_READY
}