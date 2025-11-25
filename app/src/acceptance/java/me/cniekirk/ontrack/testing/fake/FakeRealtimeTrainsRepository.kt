package me.cniekirk.ontrack.testing.fake

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import me.cniekirk.ontrack.core.domain.model.error.NetworkError
import me.cniekirk.ontrack.core.domain.model.servicedetails.ServiceDetails
import me.cniekirk.ontrack.core.domain.model.services.Platform
import me.cniekirk.ontrack.core.domain.model.services.RunDate
import me.cniekirk.ontrack.core.domain.model.services.ServiceLocation
import me.cniekirk.ontrack.core.domain.model.services.TimeStatus
import me.cniekirk.ontrack.core.domain.model.services.TrainService
import me.cniekirk.ontrack.core.domain.repository.RealtimeTrainsRepository

class FakeRealtimeTrainsRepository : RealtimeTrainsRepository {

    override suspend fun getDepartureBoardOnDateTime(
        station: String,
        year: String,
        month: String,
        day: String,
        time: String
    ): Result<List<TrainService>, NetworkError> {
        return Ok(createFakeServices())
    }

    override suspend fun getDepartureBoardOnDateTimeTo(
        fromStation: String,
        toStation: String,
        year: String,
        month: String,
        day: String,
        time: String
    ): Result<List<TrainService>, NetworkError> {
        return Ok(createFakeServices(5))
    }

    override suspend fun getArrivalBoardOnDateTime(
        station: String,
        year: String,
        month: String,
        day: String,
        time: String
    ): Result<List<TrainService>, NetworkError> {
        return Ok(createFakeServices())
    }

    override suspend fun getArrivalBoardOnDateTimeFrom(
        atStation: String,
        fromStation: String,
        year: String,
        month: String,
        day: String,
        time: String
    ): Result<List<TrainService>, NetworkError> {
        return Ok(createFakeServices(5))
    }

    override suspend fun getCurrentDepartureBoard(
        station: String
    ): Result<List<TrainService>, NetworkError> {
        return Ok(createFakeServices())
    }

    override suspend fun getCurrentDepartureBoardTo(
        fromStation: String,
        toStation: String
    ): Result<List<TrainService>, NetworkError> {
        return Ok(createFakeServices(5))
    }

    override suspend fun getCurrentArrivalBoard(
        station: String
    ): Result<List<TrainService>, NetworkError> {
        return Ok(createFakeServices())
    }

    override suspend fun getCurrentArrivalBoardFrom(
        atStation: String,
        fromStation: String
    ): Result<List<TrainService>, NetworkError> {
        return Ok(createFakeServices(5))
    }

    override suspend fun getServiceDetails(
        serviceUid: String,
        year: String,
        month: String,
        day: String
    ): Result<ServiceDetails, NetworkError> {
        // Create a minimal fake ServiceDetails
        return Ok(
            ServiceDetails(
                trainOperatingCompany = "Great Western Railway",
                origin = "London Paddington",
                destination = "Oxford",
                locations = emptyList() // Simplified for baseline profile
            )
        )
    }

    private fun createFakeServices(count: Int = 10): List<TrainService> {
        return List(count) { index ->
            TrainService(
                serviceId = "SERVICE_$index",
                runDate = RunDate(
                    day = "10",
                    month = "11",
                    year = "2025"
                ),
                origin = if (index % 2 == 0) "London Paddington" else "Reading",
                destination = if (index % 2 == 0) "Oxford" else "London Paddington",
                timeStatus = when (index % 5) {
                    0 -> TimeStatus.OnTime(scheduledTime = "${9 + index}:${(index * 5) % 60}")
                    1 -> TimeStatus.Delayed(
                        scheduledTime = "${9 + index}:00",
                        estimatedTime = "${9 + index}:${5 + index}",
                        delayInMinutes = 5 + index
                    )
                    2 -> TimeStatus.Departed(
                        actualDepartureTime = "${9 + index}:02",
                        scheduledDepartureTime = "${9 + index}:00",
                        delayInMinutes = 2
                    )
                    3 -> TimeStatus.Arrived(
                        actualArrivalTime = "${9 + index}:03",
                        scheduledArrivalTime = "${9 + index}:00",
                        delayInMinutes = 3
                    )
                    else -> TimeStatus.Unknown
                },
                platform = if (index % 3 == 0) {
                    Platform.Confirmed(
                        platformName = "${(index % 10) + 1}",
                        isChanged = false
                    )
                } else {
                    Platform.Estimated(
                        platformName = "${(index % 10) + 1}",
                        isChanged = false
                    )
                },
                serviceLocation = when (index % 5) {
                    0 -> ServiceLocation.AT_PLATFORM
                    1 -> ServiceLocation.APPROACHING_PLATFORM
                    2 -> ServiceLocation.READY_TO_DEPART
                    else -> null
                },
                trainOperatingCompany = if (index % 2 == 0) "Great Western Railway" else "CrossCountry"
            )
        }
    }
}
