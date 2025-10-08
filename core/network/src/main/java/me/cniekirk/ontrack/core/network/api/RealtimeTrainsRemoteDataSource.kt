package me.cniekirk.ontrack.core.network.api

import dev.zacsweers.metro.Inject
import me.cniekirk.ontrack.core.network.api.realtimetrains.RealtimeTrainsApi
import me.cniekirk.ontrack.core.network.model.realtimetrains.StationBoard
import retrofit2.Response

@Inject
class RealtimeTrainsRemoteDataSource(
    private val realtimeTrainsApi: RealtimeTrainsApi
) {

    suspend fun getDeparturesAtStation(
        station: String,
        year: String,
        month: String,
        day: String,
        time: String
    ): Response<StationBoard> =
        realtimeTrainsApi.getDepartures(station, year, month, day, time)
}