package me.cniekirk.ontrack.core.domain.repository

interface RealtimeTrainsRepository {

    suspend fun getDepartureBoard(
        station: String,
        year: String,
        month: String,
        day: String,
        time: String
    ): List<String>
}