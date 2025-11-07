package me.cniekirk.ontrack.core.network.model.realtimetrains.servicedetail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.cniekirk.ontrack.core.network.model.realtimetrains.common.LocationPair
import me.cniekirk.ontrack.core.network.model.realtimetrains.common.ServiceStopLocation
import me.cniekirk.ontrack.core.network.model.realtimetrains.common.ServiceType

@Serializable
data class ServiceDetail(
    @SerialName("serviceUid") val serviceUid: String,
    @SerialName("runDate") val runDate: String,
    @SerialName("serviceType") val serviceType: ServiceType,
    @SerialName("isPassenger") val isPassenger: Boolean,
    @SerialName("trainIdentity") val trainIdentity: String? = null,
    @SerialName("powerType") val powerType: String? = null,
    @SerialName("trainClass") val trainClass: String? = null,
    @SerialName("sleeper") val sleeper: String? = null,
    @SerialName("atocName") val atocName: String = "Unknown",
    @SerialName("performanceMonitored") val performanceMonitored: Boolean,
    @SerialName("origin") val origin: List<LocationPair>,
    @SerialName("destination") val destination: List<LocationPair>,
    @SerialName("locations") val locations: List<ServiceStopLocation>,
    @SerialName("realtimeActivated") val realtimeActivated: Boolean = false,
    @SerialName("runningIdentity") val runningIdentity: String? = null
)
