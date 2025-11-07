package me.cniekirk.ontrack.core.data.repository

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import me.cniekirk.ontrack.core.database.dao.StationDao
import me.cniekirk.ontrack.core.database.entity.StationEntity
import me.cniekirk.ontrack.core.domain.model.error.NetworkError
import me.cniekirk.ontrack.core.network.api.openraildata.OpenRailDataApi
import me.cniekirk.ontrack.core.network.model.openraildata.StationEntry
import me.cniekirk.ontrack.core.network.model.openraildata.StationListResponse
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class StationsRepositoryImplTest {

    private val api = mockk<OpenRailDataApi>()
    private val dao = mockk<StationDao>(relaxed = true)
    private lateinit var repository: StationsRepositoryImpl

    @Before
    fun setup() {
        repository = StationsRepositoryImpl(api, dao)
    }

    @Test
    fun `updateStations successfully fetches and stores stations`() = runTest {
        // Given
        val mockResponse = createMockStationListResponse()
        coEvery { api.getStationList(any(), any()) } returns Response.success(mockResponse)
        coEvery { dao.insertAll(any()) } returns Unit

        // When
        val result = repository.updateStations()

        // Then
        assertNotNull(result.get())
        coVerify(exactly = 1) { api.getStationList(any(), any()) }
        coVerify(exactly = 1) {
            dao.insertAll(match { stations ->
                stations.size == 3 &&
                stations[0].crs == "LDS" &&
                stations[0].name == "Leeds" &&
                stations[1].crs == "MAN" &&
                stations[1].name == "Manchester Piccadilly" &&
                stations[2].crs == "KGX" &&
                stations[2].name == "London Kings Cross"
            })
        }
    }

    @Test
    fun `updateStations returns error on API failure`() = runTest {
        // Given
        coEvery { api.getStationList(any(), any()) } returns Response.error(
            500,
            "Server error".toResponseBody()
        )

        // When
        val result = repository.updateStations()

        // Then
        val error = result.getError()
        assertNotNull(error)
        assertTrue(error is NetworkError.HttpError)
        assertEquals(500, (error as NetworkError.HttpError).code)
        coVerify(exactly = 1) { api.getStationList(any(), any()) }
        coVerify(exactly = 0) { dao.insertAll(any()) }
    }

    @Test
    fun `updateStations returns error on network exception`() = runTest {
        // Given
        coEvery { api.getStationList(any(), any()) } throws java.io.IOException("Network error")

        // When
        val result = repository.updateStations()

        // Then
        val error = result.getError()
        assertNotNull(error)
        assertTrue(error is NetworkError.NetworkFailure)
        coVerify(exactly = 1) { api.getStationList(any(), any()) }
        coVerify(exactly = 0) { dao.insertAll(any()) }
    }

    @Test
    fun `getStations with forceRefresh true updates and returns stations`() = runTest {
        // Given
        val mockResponse = createMockStationListResponse()
        val mockEntities = createMockStationEntities()

        coEvery { api.getStationList(any(), any()) } returns Response.success(mockResponse)
        coEvery { dao.insertAll(any()) } returns Unit
        coEvery { dao.getAllStations() } returns mockEntities

        // When
        val result = repository.getStations(forceRefresh = true)

        // Then
        val stations = result.get()
        assertNotNull(stations)
        assertEquals(3, stations!!.size)
        assertEquals("LDS", stations[0].crs)
        assertEquals("Leeds", stations[0].name)
        assertEquals("MAN", stations[1].crs)
        assertEquals("Manchester Piccadilly", stations[1].name)

        coVerify(exactly = 1) { api.getStationList(any(), any()) }
        coVerify(exactly = 1) { dao.insertAll(any()) }
        coVerify(exactly = 1) { dao.getAllStations() }
    }

    @Test
    fun `getStations with forceRefresh false and empty DB updates and returns stations`() = runTest {
        // Given
        val mockResponse = createMockStationListResponse()
        val mockEntities = createMockStationEntities()

        coEvery { dao.getCount() } returns 0
        coEvery { api.getStationList(any(), any()) } returns Response.success(mockResponse)
        coEvery { dao.insertAll(any()) } returns Unit
        coEvery { dao.getAllStations() } returns mockEntities

        // When
        val result = repository.getStations(forceRefresh = false)

        // Then
        val stations = result.get()
        assertNotNull(stations)
        assertEquals(3, stations!!.size)

        coVerify(exactly = 1) { dao.getCount() }
        coVerify(exactly = 1) { api.getStationList(any(), any()) }
        coVerify(exactly = 1) { dao.insertAll(any()) }
        coVerify(exactly = 1) { dao.getAllStations() }
    }

    @Test
    fun `getStations with forceRefresh false and non-empty DB returns cached stations`() = runTest {
        // Given
        val mockEntities = createMockStationEntities()

        coEvery { dao.getCount() } returns 3
        coEvery { dao.getAllStations() } returns mockEntities

        // When
        val result = repository.getStations(forceRefresh = false)

        // Then
        val stations = result.get()
        assertNotNull(stations)
        assertEquals(3, stations!!.size)
        assertEquals("LDS", stations[0].crs)
        assertEquals("Leeds", stations[0].name)

        coVerify(exactly = 1) { dao.getCount() }
        coVerify(exactly = 0) { api.getStationList(any(), any()) }
        coVerify(exactly = 0) { dao.insertAll(any()) }
        coVerify(exactly = 1) { dao.getAllStations() }
    }

    @Test
    fun `getStations returns error when forceRefresh true and API fails`() = runTest {
        // Given
        coEvery { api.getStationList(any(), any()) } returns Response.error(
            503,
            "Service unavailable".toResponseBody()
        )

        // When
        val result = repository.getStations(forceRefresh = true)

        // Then
        val error = result.getError()
        assertNotNull(error)
        assertTrue(error is NetworkError.HttpError)
        assertEquals(503, (error as NetworkError.HttpError).code)

        coVerify(exactly = 1) { api.getStationList(any(), any()) }
        coVerify(exactly = 0) { dao.getAllStations() }
    }

    @Test
    fun `getStations returns error when DB empty and API fails`() = runTest {
        // Given
        coEvery { dao.getCount() } returns 0
        coEvery { api.getStationList(any(), any()) } throws java.io.IOException("Connection timeout")

        // When
        val result = repository.getStations(forceRefresh = false)

        // Then
        val error = result.getError()
        assertNotNull(error)
        assertTrue(error is NetworkError.NetworkFailure)

        coVerify(exactly = 1) { dao.getCount() }
        coVerify(exactly = 1) { api.getStationList(any(), any()) }
        coVerify(exactly = 0) { dao.getAllStations() }
    }

    @Test
    fun `updateStations handles empty station list`() = runTest {
        // Given
        val emptyResponse = StationListResponse(
            version = "1",
            stationList = emptyList()
        )
        coEvery { api.getStationList(any(), any()) } returns Response.success(emptyResponse)
        coEvery { dao.insertAll(any()) } returns Unit

        // When
        val result = repository.updateStations()

        // Then
        assertNotNull(result.get())
        coVerify(exactly = 1) {
            dao.insertAll(match { stations -> stations.isEmpty() })
        }
    }

    @Test
    fun `getStations with forceRefresh true handles empty cached data`() = runTest {
        // Given
        val mockResponse = createMockStationListResponse()

        coEvery { api.getStationList(any(), any()) } returns Response.success(mockResponse)
        coEvery { dao.insertAll(any()) } returns Unit
        coEvery { dao.getAllStations() } returns emptyList()

        // When
        val result = repository.getStations(forceRefresh = true)

        // Then
        val stations = result.get()
        assertNotNull(stations)
        assertEquals(0, stations!!.size)

        coVerify(exactly = 1) { dao.getAllStations() }
    }

    @Test
    fun `updateStations stores correct entity mapping`() = runTest {
        // Given
        val mockResponse = StationListResponse(
            version = "1",
            stationList = listOf(
                StationEntry("LDS", "Leeds"),
                StationEntry("MAN", "Manchester Piccadilly")
            )
        )
        coEvery { api.getStationList(any(), any()) } returns Response.success(mockResponse)
        coEvery { dao.insertAll(any()) } returns Unit

        // When
        val result = repository.updateStations()

        // Then
        assertNotNull(result.get())
        coVerify(exactly = 1) {
            dao.insertAll(match { entities ->
                entities.size == 2 &&
                entities[0].crs == "LDS" &&
                entities[0].name == "Leeds" &&
                entities[1].crs == "MAN" &&
                entities[1].name == "Manchester Piccadilly"
            })
        }
    }

    // Helper functions
    private fun createMockStationListResponse(): StationListResponse {
        return StationListResponse(
            version = "1",
            stationList = listOf(
                StationEntry("LDS", "Leeds"),
                StationEntry("MAN", "Manchester Piccadilly"),
                StationEntry("KGX", "London Kings Cross")
            )
        )
    }

    private fun createMockStationEntities(): List<StationEntity> {
        return listOf(
            StationEntity("LDS", "Leeds"),
            StationEntity("MAN", "Manchester Piccadilly"),
            StationEntity("KGX", "London Kings Cross")
        )
    }
}
