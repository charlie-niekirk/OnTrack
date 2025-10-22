package me.cniekirk.ontrack.core.network.model.realtimetrains.servicelist

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val location: LocationDetail,
    val filter: Filter? = null,
    val services: List<BoardService>
)