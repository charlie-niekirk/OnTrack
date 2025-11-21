package me.cniekirk.ontrack.testing.fake

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import me.cniekirk.ontrack.core.domain.model.Station
import me.cniekirk.ontrack.core.domain.model.error.NetworkError
import me.cniekirk.ontrack.core.domain.repository.StationsRepository

class FakeStationsRepository : StationsRepository {

    private val fakeStations = listOf(
        Station(name = "London Paddington", crs = "PAD"),
        Station(name = "Reading", crs = "RDG"),
        Station(name = "Oxford", crs = "OXF"),
        Station(name = "Birmingham New Street", crs = "BHM"),
        Station(name = "Manchester Piccadilly", crs = "MAN"),
        Station(name = "Liverpool Lime Street", crs = "LIV"),
        Station(name = "Edinburgh Waverley", crs = "EDB"),
        Station(name = "Glasgow Central", crs = "GLC"),
        Station(name = "Bristol Temple Meads", crs = "BRI"),
        Station(name = "Leeds", crs = "LDS"),
        Station(name = "Cardiff Central", crs = "CDF"),
        Station(name = "Newcastle", crs = "NCL"),
        Station(name = "York", crs = "YRK"),
        Station(name = "Cambridge", crs = "CBG"),
        Station(name = "Brighton", crs = "BTN"),
    )

    override suspend fun updateStations(): Result<Unit, NetworkError> {
        // Simulate instant success
        return Ok(Unit)
    }

    override suspend fun getStations(forceRefresh: Boolean): Result<List<Station>, NetworkError> {
        // Return fake stations immediately
        return Ok(fakeStations)
    }
}
