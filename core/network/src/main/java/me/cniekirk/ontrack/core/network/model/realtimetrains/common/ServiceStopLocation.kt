package me.cniekirk.ontrack.core.network.model.realtimetrains.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceStopLocation(
    @SerialName("realtimeActivated") val realtimeActivated: Boolean = false,
    @SerialName("tiploc") val tiploc: String,
    @SerialName("crs") val crs: String? = null,
    @SerialName("description") val description: String,
    @SerialName("wttBookedArrival") val wttBookedArrival: String? = null,
    @SerialName("wttBookedDeparture") val wttBookedDeparture: String? = null,
    @SerialName("wttBookedPass") val wttBookedPass: String? = null,
    @SerialName("gbttBookedArrival") val gbttBookedArrival: String? = null,
    @SerialName("gbttBookedDeparture") val gbttBookedDeparture: String? = null,
    @SerialName("origin") val origin: List<LocationPair>,
    @SerialName("destination") val destination: List<LocationPair>,
    @SerialName("isCall") val isCall: Boolean = false,
    @SerialName("isPublicCall") val isPublicCall: Boolean = false,
    @SerialName("realtimeArrival") val realtimeArrival: String? = null,
    @SerialName("realtimeArrivalActual") val realtimeArrivalActual: Boolean = false,
    @SerialName("realtimeArrivalNoReport") val realtimeArrivalNoReport: Boolean = false,
    @SerialName("realtimeWttArrivalLateness") val realtimeWttArrivalLateness: Int? = 0,
    @SerialName("realtimeGbttArrivalLateness") val realtimeGbttArrivalLateness: Int? = 0,
    @SerialName("realtimeDeparture") val realtimeDeparture: String? = null,
    @SerialName("realtimeDepartureActual") val realtimeDepartureActual: Boolean = false,
    @SerialName("realtimeDepartureNoReport") val realtimeDepartureNoReport: Boolean = false,
    @SerialName("realtimeWttDepartureLateness") val realtimeWttDepartureLateness: Int? = 0,
    @SerialName("realtimeGbttDepartureLateness") val realtimeGbttDepartureLateness: Int? = 0,
    @SerialName("realtimePass") val realtimePass: String? = null,
    @SerialName("realtimePassActual") val realtimePassActual: Boolean = false,
    @SerialName("realtimePassNoReport") val realtimePassNoReport: Boolean = false,
    @SerialName("realtimeWttPassLateness") val realtimeWttPassLateness: Int? = 0,
    @SerialName("platform") val platform: String? = "none",
    @SerialName("platformConfirmed") val platformConfirmed: Boolean = false,
    @SerialName("platformChanged") val platformChanged: Boolean = false,
    @SerialName("line") val line: String? = "none",
    @SerialName("lineConfirmed") val lineConfirmed: Boolean = false,
    @SerialName("path") val path: String? = "none",
    @SerialName("pathConfirmed") val pathConfirmed: Boolean = false,
    @SerialName("cancelReasonCode") val cancelReasonCode: String? = null,
    @SerialName("cancelReasonShortText") val cancelReasonShortText: String? = null,
    @SerialName("cancelReasonLongText") val cancelReasonLongText: String? = null,
    @SerialName("displayAs") val displayAs: DisplayAsType,
    @SerialName("serviceLocation") val serviceLocation: ServiceLocationType? = null
) {
    val isCancelled: Boolean = cancelReasonCode != null
}