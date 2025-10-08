package me.cniekirk.ontrack.core.network.model.realtimetrains

import kotlinx.serialization.Serializable

@Serializable
data class LocationDetail(
    val realtimeActivated: Boolean,
    val tiploc: String,
    val crs: String,
    val description: String,
    val gbttBookedDeparture: String,
    val origin: List<OriginDestination>,
    val destination: List<OriginDestination>,
    val isCall: Boolean,
    val isPublicCall: Boolean,
    val realtimeDeparture: String,
    val realtimeDepartureActual: Boolean,
    val platform: String? = null,
    val platformConfirmed: Boolean,
    val platformChanged: Boolean,
    val serviceLocation: String? = null,
    val displayAs: String,
    val cancelReasonCode: String? = null,
    val cancelReasonShortText: String? = null,
    val cancelReasonLongText: String? = null,
    val associations: List<Association>? = null
)