package me.cniekirk.ontrack.core.network.api.realtimetrains

import me.cniekirk.ontrack.core.network.model.realtimetrains.StationBoard
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RealtimeTrainsApi {

    @GET("json/search/{fromStation}/{year}/{month}/{day}/{time}")
    suspend fun getDepartures(
        @Path("fromStation")
        fromStation: String,
        @Path("year")
        year: String = "",
        @Path("month")
        month: String = "",
        @Path("day")
        day: String = "",
        @Path("time")
        time: String = "",
    ): Response<StationBoard>
}