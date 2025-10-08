package me.cniekirk.ontrack.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stations")
data class StationEntity(
    @PrimaryKey val tiploc: String,
    val name: String,
    val crs: String?
)