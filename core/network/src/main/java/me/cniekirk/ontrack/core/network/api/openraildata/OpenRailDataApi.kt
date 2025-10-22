package me.cniekirk.ontrack.core.network.api.openraildata

import me.cniekirk.ontrack.core.network.model.openraildata.StationListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

private const val API_KEY = "q7mYGMISDVnw6RF1hVWm4rfwbTcy7pAYeZ7Arb9EG1kEtoNY"

interface OpenRailDataApi {

    @GET("LDBSVWS/api/ref/20211101/GetStationList/{currentVersion}")
    suspend fun getStationList(
        @Header("x-apikey") apiKey: String = API_KEY,
        @Path("currentVersion") currentVersion: String = "1",
    ): Response<StationListResponse>
}