package me.cniekirk.ontrack.core.data.repository

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import me.cniekirk.ontrack.core.domain.model.error.NetworkError
import me.cniekirk.ontrack.core.network.api.realtimetrains.RealtimeTrainsApi
import me.cniekirk.ontrack.core.network.model.realtimetrains.common.DisplayAsType
import me.cniekirk.ontrack.core.network.model.realtimetrains.common.LocationPair
import me.cniekirk.ontrack.core.network.model.realtimetrains.common.ServiceLocationType
import me.cniekirk.ontrack.core.network.model.realtimetrains.common.ServiceStopLocation
import me.cniekirk.ontrack.core.network.model.realtimetrains.servicedetail.ServiceDetail
import me.cniekirk.ontrack.core.network.model.realtimetrains.servicelist.BoardService
import me.cniekirk.ontrack.core.network.model.realtimetrains.servicelist.LocationDetail
import me.cniekirk.ontrack.core.network.model.realtimetrains.servicelist.SearchResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class RealtimeTrainsRepositoryImplTest {

    private val api = mockk<RealtimeTrainsApi>()
    private val serviceMapper = mockk<me.cniekirk.ontrack.core.data.mapper.ServiceMapper>()
    private lateinit var repository: RealtimeTrainsRepositoryImpl

    @Before
    fun setup() {
        // Setup default mock behavior for serviceMapper
        coEvery {
            serviceMapper.toTrainService(any(), any())
        } returns createMockTrainService()

        repository = RealtimeTrainsRepositoryImpl(api, serviceMapper)
    }

    @Test
    fun `getDepartureBoardOnDateTime returns mapped services on success`() = runTest {
        // Given
        val mockResponse = createMockBoardResponse()
        coEvery {
            api.getDeparturesOnDateTime("LDS", "2024", "01", "15", "1200")
        } returns Response.success(mockResponse)

        // When
        val result = repository.getDepartureBoardOnDateTime(
            station = "LDS",
            year = "2024",
            month = "01",
            day = "15",
            time = "1200"
        )

        // Then
        val services = result.get()
        assertNotNull(services)
        assertEquals(1, services!!.size)
        assertEquals("TEST123", services[0].serviceId)
        assertEquals("Leeds", services[0].origin)
        assertEquals("London Kings Cross", services[0].destination)

        coVerify(exactly = 1) {
            api.getDeparturesOnDateTime("LDS", "2024", "01", "15", "1200")
        }
    }

    @Test
    fun `getDepartureBoardOnDateTime returns error on API failure`() = runTest {
        // Given
        coEvery {
            api.getDeparturesOnDateTime(any(), any(), any(), any(), any())
        } throws Exception("Network error")

        // When
        val result = repository.getDepartureBoardOnDateTime(
            station = "LDS",
            year = "2024",
            month = "01",
            day = "15",
            time = "1200"
        )

        // Then
        val error = result.getError()
        assertNotNull(error)
        assertTrue(error is NetworkError)
    }

    @Test
    fun `getDepartureBoardOnDateTimeTo returns mapped services on success`() = runTest {
        // Given
        val mockResponse = createMockBoardResponse()
        coEvery {
            api.getDeparturesToOnDateTime("LDS", "KGX", "2024", "01", "15", "1200")
        } returns Response.success(mockResponse)

        // When
        val result = repository.getDepartureBoardOnDateTimeTo(
            fromStation = "LDS",
            toStation = "KGX",
            year = "2024",
            month = "01",
            day = "15",
            time = "1200"
        )

        // Then
        val services = result.get()
        assertNotNull(services)
        assertEquals(1, services!!.size)

        coVerify(exactly = 1) {
            api.getDeparturesToOnDateTime("LDS", "KGX", "2024", "01", "15", "1200")
        }
    }

    @Test
    fun `getArrivalBoardOnDateTime returns mapped services with isArrival true`() = runTest {
        // Given
        val mockResponse = createMockBoardResponse()
        coEvery {
            api.getArrivalsOnDateTime("LDS", "2024", "01", "15", "1200")
        } returns Response.success(mockResponse)

        // When
        val result = repository.getArrivalBoardOnDateTime(
            station = "LDS",
            year = "2024",
            month = "01",
            day = "15",
            time = "1200"
        )

        // Then
        val services = result.get()
        assertNotNull(services)
        assertEquals(1, services!!.size)

        coVerify(exactly = 1) {
            api.getArrivalsOnDateTime("LDS", "2024", "01", "15", "1200")
        }
    }

    @Test
    fun `getArrivalBoardOnDateTimeFrom returns mapped services on success`() = runTest {
        // Given
        val mockResponse = createMockBoardResponse()
        coEvery {
            api.getArrivalsFromOnDateTime("LDS", "KGX", "2024", "01", "15", "1200")
        } returns Response.success(mockResponse)

        // When
        val result = repository.getArrivalBoardOnDateTimeFrom(
            atStation = "LDS",
            fromStation = "KGX",
            year = "2024",
            month = "01",
            day = "15",
            time = "1200"
        )

        // Then
        val services = result.get()
        assertNotNull(services)
        assertEquals(1, services!!.size)

        coVerify(exactly = 1) {
            api.getArrivalsFromOnDateTime("LDS", "KGX", "2024", "01", "15", "1200")
        }
    }

    @Test
    fun `getCurrentDepartureBoard returns mapped services on success`() = runTest {
        // Given
        val mockResponse = createMockBoardResponse()
        coEvery {
            api.getCurrentDepartures("LDS")
        } returns Response.success(mockResponse)

        // When
        val result = repository.getCurrentDepartureBoard("LDS")

        // Then
        val services = result.get()
        assertNotNull(services)
        assertEquals(1, services!!.size)

        coVerify(exactly = 1) {
            api.getCurrentDepartures("LDS")
        }
    }

    @Test
    fun `getCurrentDepartureBoardTo returns mapped services on success`() = runTest {
        // Given
        val mockResponse = createMockBoardResponse()
        coEvery {
            api.getCurrentDeparturesTo("LDS", "KGX")
        } returns Response.success(mockResponse)

        // When
        val result = repository.getCurrentDepartureBoardTo(
            fromStation = "LDS",
            toStation = "KGX"
        )

        // Then
        val services = result.get()
        assertNotNull(services)
        assertEquals(1, services!!.size)

        coVerify(exactly = 1) {
            api.getCurrentDeparturesTo("LDS", "KGX")
        }
    }

    @Test
    fun `getCurrentArrivalBoard returns mapped services on success`() = runTest {
        // Given
        val mockResponse = createMockBoardResponse()
        coEvery {
            api.getCurrentArrivals("LDS")
        } returns Response.success(mockResponse)

        // When
        val result = repository.getCurrentArrivalBoard("LDS")

        // Then
        val services = result.get()
        assertNotNull(services)
        assertEquals(1, services!!.size)

        coVerify(exactly = 1) {
            api.getCurrentArrivals("LDS")
        }
    }

    @Test
    fun `getCurrentArrivalBoardFrom returns mapped services on success`() = runTest {
        // Given
        val mockResponse = createMockBoardResponse()
        coEvery {
            api.getCurrentArrivalsFrom("LDS", "KGX")
        } returns Response.success(mockResponse)

        // When
        val result = repository.getCurrentArrivalBoardFrom(
            atStation = "LDS",
            fromStation = "KGX"
        )

        // Then
        val services = result.get()
        assertNotNull(services)
        assertEquals(1, services!!.size)

        coVerify(exactly = 1) {
            api.getCurrentArrivalsFrom("LDS", "KGX")
        }
    }

    @Test
    fun `getServiceDetails returns mapped service details on success`() = runTest {
        // Given
        val mockServiceDetail = createMockServiceDetail()
        coEvery {
            api.getServiceDetails("TEST123", "2024", "01", "15")
        } returns Response.success(mockServiceDetail)

        // When
        val result = repository.getServiceDetails(
            serviceUid = "TEST123",
            year = "2024",
            month = "01",
            day = "15"
        )

        // Then
        val serviceDetails = result.get()
        assertNotNull(serviceDetails)
        assertEquals("LNER", serviceDetails!!.trainOperatingCompany)
        assertEquals("Leeds", serviceDetails.origin)
        assertEquals("London Kings Cross", serviceDetails.destination)

        coVerify(exactly = 1) {
            api.getServiceDetails("TEST123", "2024", "01", "15")
        }
    }

    @Test
    fun `getServiceDetails returns error on API failure`() = runTest {
        // Given
        coEvery {
            api.getServiceDetails(any(), any(), any(), any())
        } throws Exception("Service not found")

        // When
        val result = repository.getServiceDetails(
            serviceUid = "TEST123",
            year = "2024",
            month = "01",
            day = "15"
        )

        // Then
        val error = result.getError()
        assertNotNull(error)
        assertTrue(error is NetworkError)
    }

    @Test
    fun `getCurrentDepartureBoard handles empty services list`() = runTest {
        // Given
        val emptyResponse = SearchResponse(
            location = createMockLocation(),
            services = emptyList()
        )
        coEvery {
            api.getCurrentDepartures("LDS")
        } returns Response.success(emptyResponse)

        // When
        val result = repository.getCurrentDepartureBoard("LDS")

        // Then
        val services = result.get()
        assertNotNull(services)
        assertEquals(0, services!!.size)
    }

    // Helper functions to create mock data
    private fun createMockBoardResponse(): SearchResponse {
        return SearchResponse(
            location = createMockLocation(),
            services = listOf(createMockBoardService())
        )
    }

    private fun createMockLocation(): LocationDetail {
        return LocationDetail(
            name = "Leeds",
            crs = "LDS",
        )
    }

    private fun createMockOrigin(): List<LocationPair> = listOf(LocationPair("LDS", "Leeds", "1111", "1111"))
    private fun createMockDestination(): List<LocationPair> = listOf(LocationPair("KGX", "London Kings Cross", "2222", "2222"))

    private fun createMockBoardService(): BoardService {
        return BoardService(
            locationDetail = ServiceStopLocation(
                realtimeActivated = true,
                tiploc = "LEEDS",
                crs = "LDS",
                description = "Leeds",
                gbttBookedArrival = null,
                gbttBookedDeparture = "1200",
                origin = createMockOrigin(),
                destination = createMockDestination(),
                isCall = true,
                isPublicCall = true,
                realtimeArrival = null,
                realtimeArrivalActual = false,
                realtimeDeparture = "1200",
                realtimeDepartureActual = false,
                platform = "5",
                platformConfirmed = true,
                platformChanged = false,
                displayAs = DisplayAsType.CALL,
                serviceLocation = ServiceLocationType.AT_PLAT,
                cancelReasonCode = null,
                cancelReasonShortText = null,
                cancelReasonLongText = null
            ),
            serviceUid = "TEST123",
            runDate = "2024-01-15",
            trainIdentity = "1A23",
            runningIdentity = "1A23",
            atocCode = "LN",
            atocName = "LNER",
            serviceType = me.cniekirk.ontrack.core.network.model.realtimetrains.common.ServiceType.TRAIN,
            isPassenger = true
        )
    }

    private fun createMockServiceDetail(): ServiceDetail {
        return ServiceDetail(
            serviceUid = "TEST123",
            runDate = "2024-01-15",
            serviceType = me.cniekirk.ontrack.core.network.model.realtimetrains.common.ServiceType.TRAIN,
            isPassenger = true,
            trainIdentity = "1A23",
            powerType = "EMU",
            trainClass = "XXX",
            sleeper = null,
            atocName = "LNER",
            performanceMonitored = true,
            origin = createMockOrigin(),
            destination = createMockDestination(),
            locations = emptyList(),
            realtimeActivated = true,
            runningIdentity = "1A23"
        )
    }

    private fun createMockTrainService(): me.cniekirk.ontrack.core.domain.model.services.TrainService {
        return me.cniekirk.ontrack.core.domain.model.services.TrainService(
            serviceId = "TEST123",
            runDate = me.cniekirk.ontrack.core.domain.model.services.RunDate("15", "01", "2024"),
            origin = "Leeds",
            destination = "London Kings Cross",
            timeStatus = me.cniekirk.ontrack.core.domain.model.services.TimeStatus.OnTime("1200"),
            platform = me.cniekirk.ontrack.core.domain.model.services.Platform.Confirmed("5", false),
            serviceLocation = me.cniekirk.ontrack.core.domain.model.services.ServiceLocation.AT_PLATFORM,
            trainOperatingCompany = "LNER"
        )
    }
}
