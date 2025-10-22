package me.cniekirk.ontrack.feature.servicelist

import me.cniekirk.ontrack.core.domain.model.services.TrainService
import me.cniekirk.ontrack.core.navigation.ServiceListType

data class ServiceListState(
    val isLoading: Boolean = true,
    val trainServiceList: List<TrainService> = emptyList(),
    val filterStation: String? = null,
    val targetStation: String,
    val serviceListType: ServiceListType
)
