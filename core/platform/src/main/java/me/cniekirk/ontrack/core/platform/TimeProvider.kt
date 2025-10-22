package me.cniekirk.ontrack.core.platform

import java.time.LocalDate

interface TimeProvider {

    fun currentDateMillis(): Long

    fun convertMillisToDate(millis: Long): LocalDate
}