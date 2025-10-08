package me.cniekirk.ontrack.core.data.mapper

import me.cniekirk.ontrack.core.database.entity.StationEntity
import me.cniekirk.ontrack.core.domain.model.Station

fun StationEntity.toStation(): Station = Station(name, crs, tiploc)