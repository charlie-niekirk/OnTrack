package me.cniekirk.ontrack.core.network.api.realtimetrains

import me.cniekirk.ontrack.core.network.model.realtimetrains.servicelist.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RealtimeTrainsApi {

    @GET("api/v1/json/search/{location}/to/{toLocation}/{year}/{month}/{day}/{time}")
    suspend fun getDeparturesToOnDateTime(
        @Path("location") location: String,
        @Path("toLocation") toLocation: String,
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("day") day: String,
        @Path("time") time: String
    ): Response<SearchResponse>

    @GET("api/v1/json/search/{location}/{year}/{month}/{day}/{time}")
    suspend fun getDeparturesOnDateTime(
        @Path("location") location: String,
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("day") day: String,
        @Path("time") time: String
    ): Response<SearchResponse>

    @GET("api/v1/json/search/{location}")
    suspend fun getCurrentDepartures(
        @Path("location") location: String
    ): Response<SearchResponse>

    @GET("api/v1/json/search/{location}/to/{toLocation}")
    suspend fun getCurrentDeparturesTo(
        @Path("location") location: String,
        @Path("toLocation") toLocation: String
    ): Response<SearchResponse>

    @GET("api/v1/json/search/{location}/from/{fromLocation}/{year}/{month}/{day}/{time}/arrivals")
    suspend fun getArrivalsFromOnDateTime(
        @Path("location") location: String,
        @Path("fromLocation") fromLocation: String,
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("day") day: String,
        @Path("time") time: String
    ): Response<SearchResponse>

    @GET("api/v1/json/search/{location}/{year}/{month}/{day}/{time}/arrivals")
    suspend fun getArrivalsOnDateTime(
        @Path("location") location: String,
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("day") day: String,
        @Path("time") time: String
    ): Response<SearchResponse>

    @GET("api/v1/json/search/{location}/arrivals")
    suspend fun getCurrentArrivals(
        @Path("location") location: String
    ): Response<SearchResponse>

    @GET("api/v1/json/search/{location}/from/{fromLocation}/arrivals")
    suspend fun getCurrentArrivalsFrom(
        @Path("location") location: String,
        @Path("fromLocation") fromLocation: String
    ): Response<SearchResponse>

    @GET("api/v1/json/service/{serviceUid}/{year}/{month}/{day}")
    suspend fun getServiceDetails(
        @Path("serviceUid") serviceUid: String,
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("day") day: String
    )
}