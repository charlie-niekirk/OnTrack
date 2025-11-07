package me.cniekirk.ontrack.core.data.mapper

import me.cniekirk.ontrack.core.datastore.RecentSearches
import me.cniekirk.ontrack.core.domain.model.arguments.RequestTime
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceListRequest
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceListType
import me.cniekirk.ontrack.core.domain.model.arguments.TrainStation

fun ServiceListRequest.toDatastoreListRequest(): RecentSearches.ServiceListRequest {
    val listType = when (serviceListType) {
        ServiceListType.DEPARTURES -> RecentSearches.ServiceListType.DEPARTURES
        ServiceListType.ARRIVALS -> RecentSearches.ServiceListType.ARRIVALS
    }

    val targetStationProto = RecentSearches.TrainStation.newBuilder()
        .setCrs(targetStation.crs)
        .setName(targetStation.name)
        .build()

    val builder = RecentSearches.ServiceListRequest.newBuilder()
        .setServiceListType(listType)
        .setTargetStation(targetStationProto)

    // Add filter station if present
    filterStation?.let { filter ->
        val filterStationProto = RecentSearches.TrainStation.newBuilder()
            .setCrs(filter.crs)
            .setName(filter.name)
            .build()
        builder.setFilterStation(filterStationProto)
    }

    // Add request time if it's AtTime (we don't store Now since it's not useful for recent searches)
    if (requestTime is RequestTime.AtTime) {
        val newRequestTime = requestTime as RequestTime.AtTime
        val requestTimeProto = RecentSearches.RequestTime.newBuilder()
            .setMinute(newRequestTime.mins)
            .setHour(newRequestTime.hours)
            .setDay(newRequestTime.day)
            .setMonth(newRequestTime.month)
            .setYear(newRequestTime.year)
            .build()
        builder.setRequestTime(requestTimeProto)
    }

    return builder.build()
}

fun RecentSearches.ServiceListRequest.toDomainModel(): ServiceListRequest {
    val listType = when (serviceListType) {
        RecentSearches.ServiceListType.DEPARTURES -> ServiceListType.DEPARTURES
        RecentSearches.ServiceListType.ARRIVALS -> ServiceListType.ARRIVALS
        else -> ServiceListType.DEPARTURES // Default fallback
    }

    val targetStationDomain = TrainStation(
        crs = targetStation.crs,
        name = targetStation.name
    )

    val filterStationDomain = if (hasFilterStation()) {
        TrainStation(
            crs = filterStation.crs,
            name = filterStation.name
        )
    } else {
        null
    }

    val requestTimeDomain = if (hasRequestTime()) {
        RequestTime.AtTime(
            year = requestTime.year,
            month = requestTime.month,
            day = requestTime.day,
            hours = requestTime.hour,
            mins = requestTime.minute
        )
    } else {
        RequestTime.Now
    }

    return ServiceListRequest(
        serviceListType = listType,
        requestTime = requestTimeDomain,
        targetStation = targetStationDomain,
        filterStation = filterStationDomain
    )
}
