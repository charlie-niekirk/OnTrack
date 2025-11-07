package me.cniekirk.ontrack.core.data.repository

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import me.cniekirk.ontrack.core.data.mapper.ServiceMapper
import me.cniekirk.ontrack.core.data.mapper.toServiceDetails
import me.cniekirk.ontrack.core.data.util.safeApiCall
import me.cniekirk.ontrack.core.domain.model.error.NetworkError
import me.cniekirk.ontrack.core.domain.model.servicedetails.ServiceDetails
import me.cniekirk.ontrack.core.domain.model.services.TrainService
import me.cniekirk.ontrack.core.domain.repository.RealtimeTrainsRepository
import me.cniekirk.ontrack.core.network.api.realtimetrains.RealtimeTrainsApi

internal class RealtimeTrainsRepositoryImpl(
    private val realtimeTrainsApi: RealtimeTrainsApi,
    private val serviceMapper: ServiceMapper
) : RealtimeTrainsRepository {

    override suspend fun getDepartureBoardOnDateTime(
        station: String,
        year: String,
        month: String,
        day: String,
        time: String
    ): Result<List<TrainService>, NetworkError> {
        return safeApiCall {
            realtimeTrainsApi.getDeparturesOnDateTime(
                location = station,
                year = year,
                month = month,
                day = day,
                time = time
            )
        }.map { response -> response.services.map { serviceMapper.toTrainService(it) } }
    }

    override suspend fun getDepartureBoardOnDateTimeTo(
        fromStation: String,
        toStation: String,
        year: String,
        month: String,
        day: String,
        time: String
    ): Result<List<TrainService>, NetworkError> {
        return safeApiCall {
            realtimeTrainsApi.getDeparturesToOnDateTime(
                location = fromStation,
                toLocation = toStation,
                year = year,
                month = month,
                day = day,
                time = time
            )
        }.map { response -> response.services.map { serviceMapper.toTrainService(it) } }
    }

    override suspend fun getArrivalBoardOnDateTime(
        station: String,
        year: String,
        month: String,
        day: String,
        time: String
    ): Result<List<TrainService>, NetworkError> {
        return safeApiCall {
            realtimeTrainsApi.getArrivalsOnDateTime(
                location = station,
                year = year,
                month = month,
                day = day,
                time = time
            )
        }.map { response -> response.services.map { serviceMapper.toTrainService(it, isArrival = true) } }
    }

    override suspend fun getArrivalBoardOnDateTimeFrom(
        atStation: String,
        fromStation: String,
        year: String,
        month: String,
        day: String,
        time: String
    ): Result<List<TrainService>, NetworkError> {
        return safeApiCall {
            realtimeTrainsApi.getArrivalsFromOnDateTime(
                location = atStation,
                fromLocation = fromStation,
                year = year,
                month = month,
                day = day,
                time = time
            )
        }.map { response -> response.services.map { serviceMapper.toTrainService(it, isArrival = true) } }
    }

    override suspend fun getCurrentDepartureBoard(station: String): Result<List<TrainService>, NetworkError> {
        return safeApiCall {
            realtimeTrainsApi.getCurrentDepartures(location = station)
        }.map { response -> response.services.map { serviceMapper.toTrainService(it) } }
    }

    override suspend fun getCurrentDepartureBoardTo(
        fromStation: String,
        toStation: String
    ): Result<List<TrainService>, NetworkError> {
        return safeApiCall {
            realtimeTrainsApi.getCurrentDeparturesTo(
                location = fromStation,
                toLocation = toStation
            )
        }.map { response -> response.services.map { serviceMapper.toTrainService(it) } }
    }

    override suspend fun getCurrentArrivalBoard(station: String): Result<List<TrainService>, NetworkError> {
        return safeApiCall {
            realtimeTrainsApi.getCurrentArrivals(location = station)
        }.map { response -> response.services.map { serviceMapper.toTrainService(it, isArrival = true) } }
    }

    override suspend fun getCurrentArrivalBoardFrom(
        atStation: String,
        fromStation: String
    ): Result<List<TrainService>, NetworkError> {
        return safeApiCall {
            realtimeTrainsApi.getCurrentArrivalsFrom(
                location = atStation,
                fromLocation = fromStation
            )
        }.map { response -> response.services.map { serviceMapper.toTrainService(it, isArrival = true) } }
    }

    override suspend fun getServiceDetails(
        serviceUid: String,
        year: String,
        month: String,
        day: String
    ): Result<ServiceDetails, NetworkError> {
        return safeApiCall {
            realtimeTrainsApi.getServiceDetails(
                serviceUid = serviceUid,
                year = year,
                month = month,
                day = day
            )
        }.map { response -> response.toServiceDetails() }
    }
}