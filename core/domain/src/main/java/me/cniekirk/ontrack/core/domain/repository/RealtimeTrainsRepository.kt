package me.cniekirk.ontrack.core.domain.repository

import com.github.michaelbull.result.Result
import me.cniekirk.ontrack.core.domain.model.error.NetworkError
import me.cniekirk.ontrack.core.domain.model.servicedetails.ServiceDetails
import me.cniekirk.ontrack.core.domain.model.services.TrainService

interface RealtimeTrainsRepository {

    suspend fun getDepartureBoardOnDateTime(
        station: String,
        year: String,
        month: String,
        day: String,
        time: String
    ): Result<List<TrainService>, NetworkError>

    suspend fun getDepartureBoardOnDateTimeTo(
        fromStation: String,
        toStation: String,
        year: String,
        month: String,
        day: String,
        time: String
    ): Result<List<TrainService>, NetworkError>

    suspend fun getArrivalBoardOnDateTime(
        station: String,
        year: String,
        month: String,
        day: String,
        time: String
    ): Result<List<TrainService>, NetworkError>

    suspend fun getArrivalBoardOnDateTimeFrom(
        atStation: String,
        fromStation: String,
        year: String,
        month: String,
        day: String,
        time: String
    ): Result<List<TrainService>, NetworkError>

    suspend fun getCurrentDepartureBoard(
        station: String
    ): Result<List<TrainService>, NetworkError>

    suspend fun getCurrentDepartureBoardTo(
        fromStation: String,
        toStation: String
    ): Result<List<TrainService>, NetworkError>

    suspend fun getCurrentArrivalBoard(
        station: String
    ): Result<List<TrainService>, NetworkError>

    suspend fun getCurrentArrivalBoardFrom(
        atStation: String,
        fromStation: String
    ): Result<List<TrainService>, NetworkError>

    suspend fun getServiceDetails(
        serviceUid: String,
        year: String,
        month: String,
        day: String
    ): Result<ServiceDetails, NetworkError>
}