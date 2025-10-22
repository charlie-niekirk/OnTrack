package me.cniekirk.ontrack.core.platform

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

internal class LocalTimeProvider : TimeProvider {

    override fun currentDateMillis(): Long {
        return LocalDate.now(ZoneId.systemDefault())
            .atTime(5, 0)
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
    }

    override fun convertMillisToDate(millis: Long): LocalDate {
        val instant = Instant.ofEpochMilli(millis)
        val zoneId = ZoneId.systemDefault() // Or use a specific time zone if needed
        return instant.atZone(zoneId).toLocalDate()
    }
}