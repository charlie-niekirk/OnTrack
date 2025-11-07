package me.cniekirk.ontrack.core.network.model.realtimetrains.servicelist

import kotlinx.serialization.Serializable
import me.cniekirk.ontrack.core.network.model.realtimetrains.common.ServiceStopLocation
import me.cniekirk.ontrack.core.network.model.realtimetrains.common.ServiceType

@Serializable
data class BoardService(
    val locationDetail: ServiceStopLocation,
    val serviceUid: String,
    val runDate: String,
    val trainIdentity: String? = null,
    val runningIdentity: String? = null,
    val atocCode: String,
    val atocName: String,
    val serviceType: ServiceType,
    val isPassenger: Boolean,
    val plannedCancel: Boolean = false,
//    val origin: List<Stop>,
//    val destination: List<Stop>,
    val countdownMinutes: Int? = null
)