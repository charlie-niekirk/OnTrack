package me.cniekirk.ontrack.core.platform

import java.time.LocalDate

interface TimeProvider {

    fun currentDateMillis(): Long

    fun convertMillisToDate(millis: Long): LocalDate

    /**
     * Parses a date string in YYYY-MM-DD format into a LocalDate object.
     *
     * @param dateString The date string in YYYY-MM-DD format (e.g., "2024-03-15")
     * @return A LocalDate object parsed from the date string
     */
    fun parseRunDate(dateString: String): LocalDate
}