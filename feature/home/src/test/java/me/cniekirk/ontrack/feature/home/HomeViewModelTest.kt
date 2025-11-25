package me.cniekirk.ontrack.feature.home

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import me.cniekirk.ontrack.core.domain.model.Station
import me.cniekirk.ontrack.core.domain.model.arguments.RequestTime
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceListRequest
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceListType
import me.cniekirk.ontrack.core.domain.model.arguments.TrainStation
import me.cniekirk.ontrack.core.domain.model.error.LocalDataError
import me.cniekirk.ontrack.core.domain.repository.RecentSearchesRepository
import me.cniekirk.ontrack.core.navigation.StationType
import me.cniekirk.ontrack.core.platform.TimeProvider
import me.cniekirk.ontrack.feature.home.state.HomeEffect
import me.cniekirk.ontrack.feature.home.state.QueryType
import me.cniekirk.ontrack.feature.home.state.StationSelection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.orbitmvi.orbit.test.test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val recentSearchesRepository = mockk<RecentSearchesRepository>()
    private val timeProvider = mockk<TimeProvider>()
    private lateinit var viewModel: HomeViewModel

    @Test
    fun `onCreate should fetch recent searches and update recentSearches`() = runTest {
        val testRequests = listOf(createStationListRequest())

        every { timeProvider.currentDateMillis() } returns TEST_CURRENT_DATE_MILLIS
        coEvery { recentSearchesRepository.getRecentSearches() } returns flowOf(testRequests)
        viewModel = HomeViewModel(recentSearchesRepository, timeProvider)

        viewModel.test(this) {
            runOnCreate()

            expectState { copy(recentSearches = testRequests) }
        }
    }

    @Test
    fun `updateQueryType should update query type`() = runTest {
        // Given
        every { timeProvider.currentDateMillis() } returns TEST_CURRENT_DATE_MILLIS
        coEvery { recentSearchesRepository.getRecentSearches() } returns flowOf(emptyList())
        viewModel = HomeViewModel(recentSearchesRepository, timeProvider)

        // When
        viewModel.test(this) {
            containerHost.updateQueryType(QueryType.ARRIVALS)

            // Then
            val state = awaitState()
            assertEquals(QueryType.ARRIVALS, state.queryType)
        }
    }

    @Test
    fun `stationSelected should update filter station`() = runTest {
        // Given
        every { timeProvider.currentDateMillis() } returns TEST_CURRENT_DATE_MILLIS
        coEvery { recentSearchesRepository.getRecentSearches() } returns flowOf(emptyList())
        viewModel = HomeViewModel(recentSearchesRepository, timeProvider)
        val station = createTestStation(STATION_NAME_MANCHESTER, STATION_CRS_MANCHESTER)

        // When
        viewModel.test(this) {
            containerHost.stationSelected(StationType.FILTER, station)

            // Then
            val state = awaitState()
            assertTrue(state.filterStationSelection is StationSelection.Selected)
            assertEquals(station, (state.filterStationSelection as StationSelection.Selected).station)
            assertEquals(StationSelection.None, state.targetStationSelection)
        }
    }

    @Test
    fun `clearTargetStation should reset target station to None`() = runTest {
        // Given
        every { timeProvider.currentDateMillis() } returns TEST_CURRENT_DATE_MILLIS
        coEvery { recentSearchesRepository.getRecentSearches() } returns flowOf(emptyList())
        viewModel = HomeViewModel(recentSearchesRepository, timeProvider)
        val station = createTestStation(STATION_NAME_LEEDS, STATION_CRS_LEEDS)

        // When
        viewModel.test(this) {
            // First select a station
            containerHost.stationSelected(StationType.TARGET, station)
            awaitState()

            // Then clear it
            containerHost.clearTargetStation()

            // Then
            val state = awaitState()
            assertEquals(StationSelection.None, state.targetStationSelection)
        }
    }

    @Test
    fun `clearFilterStation should reset filter station to None`() = runTest {
        // Given
        every { timeProvider.currentDateMillis() } returns TEST_CURRENT_DATE_MILLIS
        coEvery { recentSearchesRepository.getRecentSearches() } returns flowOf(emptyList())
        viewModel = HomeViewModel(recentSearchesRepository, timeProvider)
        val station = createTestStation(STATION_NAME_MANCHESTER, STATION_CRS_MANCHESTER)

        // When
        viewModel.test(this) {
            // First select a station
            containerHost.stationSelected(StationType.FILTER, station)
            awaitState()

            // Then clear it
            containerHost.clearFilterStation()

            // Then
            val state = awaitState()
            assertEquals(StationSelection.None, state.filterStationSelection)
        }
    }

    @Test
    fun `processSelectedDateTime should create AtTime request with correct values`() = runTest {
        // Given
        every { timeProvider.currentDateMillis() } returns TEST_CURRENT_DATE_MILLIS
        every { timeProvider.convertMillisToDate(TEST_DATE_MILLIS) } returns TEST_LOCAL_DATE
        coEvery { recentSearchesRepository.getRecentSearches() } returns flowOf(emptyList())
        viewModel = HomeViewModel(recentSearchesRepository, timeProvider)

        // When
        viewModel.test(this) {
            containerHost.processSelectedDateTime(TEST_DATE_MILLIS, TEST_HOUR, TEST_MINUTE)

            // Then
            val state = awaitState()
            assertTrue(state.requestTime is RequestTime.AtTime)
            val atTime = state.requestTime as RequestTime.AtTime
            assertEquals(EXPECTED_YEAR, atTime.year)
            assertEquals(EXPECTED_MONTH, atTime.month)
            assertEquals(EXPECTED_DAY, atTime.day)
            assertEquals(EXPECTED_HOURS_PADDED, atTime.hours)
            assertEquals(EXPECTED_MINS_PADDED, atTime.mins)
        }
    }

    @Test
    fun `processSelectedDateTime should pad single digit hour and minute with zero`() = runTest {
        // Given
        every { timeProvider.currentDateMillis() } returns TEST_CURRENT_DATE_MILLIS
        every { timeProvider.convertMillisToDate(TEST_DATE_MILLIS) } returns TEST_LOCAL_DATE
        coEvery { recentSearchesRepository.getRecentSearches() } returns flowOf(emptyList())
        viewModel = HomeViewModel(recentSearchesRepository, timeProvider)

        // When
        viewModel.test(this) {
            containerHost.processSelectedDateTime(TEST_DATE_MILLIS, SINGLE_DIGIT_HOUR, SINGLE_DIGIT_MINUTE)

            // Then
            val state = awaitState()
            assertTrue(state.requestTime is RequestTime.AtTime)
            val atTime = state.requestTime as RequestTime.AtTime
            assertEquals(EXPECTED_SINGLE_HOUR_PADDED, atTime.hours)
            assertEquals(EXPECTED_SINGLE_MIN_PADDED, atTime.mins)
        }
    }

    @Test
    fun `resetDateTime should reset request time to Now`() = runTest {
        // Given
        every { timeProvider.currentDateMillis() } returns TEST_CURRENT_DATE_MILLIS
        every { timeProvider.convertMillisToDate(TEST_DATE_MILLIS) } returns TEST_LOCAL_DATE
        coEvery { recentSearchesRepository.getRecentSearches() } returns flowOf(emptyList())
        viewModel = HomeViewModel(recentSearchesRepository, timeProvider)

        // When
        viewModel.test(this) {
            // First set a specific time
            containerHost.processSelectedDateTime(TEST_DATE_MILLIS, TEST_HOUR, TEST_MINUTE)
            awaitState()

            // Then reset it
            containerHost.resetDateTime()

            // Then
            val state = awaitState()
            assertEquals(RequestTime.Now, state.requestTime)
        }
    }

    @Test
    fun `searchTrains with no target station should post ShowNoStationSelectedError side effect`() = runTest {
        // Given
        every { timeProvider.currentDateMillis() } returns TEST_CURRENT_DATE_MILLIS
        coEvery { recentSearchesRepository.getRecentSearches() } returns flowOf(emptyList())
        viewModel = HomeViewModel(recentSearchesRepository, timeProvider)

        // When
        viewModel.test(this) {
            containerHost.searchTrains()

            expectSideEffect(HomeEffect.ShowNoStationSelectedError)
        }
    }

    @Test
    fun `searchTrains with target station and no filter should post NavigateToServiceList effect for DEPARTURES`() = runTest {
        // Given
        every { timeProvider.currentDateMillis() } returns TEST_CURRENT_DATE_MILLIS
        coEvery { recentSearchesRepository.getRecentSearches() } returns flowOf(emptyList())
        viewModel = HomeViewModel(recentSearchesRepository, timeProvider)
        val targetStation = createTestStation(STATION_NAME_LEEDS, STATION_CRS_LEEDS)

        // When
        viewModel.test(this) {
            // Select target station
            containerHost.stationSelected(StationType.TARGET, targetStation)
            awaitState()

            // Search trains
            containerHost.searchTrains()

            // Then
            val effect = awaitSideEffect()
            assertTrue(effect is HomeEffect.NavigateToServiceList)
            val navigateEffect = effect as HomeEffect.NavigateToServiceList
            assertEquals(ServiceListType.DEPARTURES, navigateEffect.serviceListRequest.serviceListType)
            assertEquals(RequestTime.Now, navigateEffect.serviceListRequest.requestTime)
            assertEquals(STATION_CRS_LEEDS, navigateEffect.serviceListRequest.targetStation.crs)
            assertEquals(STATION_NAME_LEEDS, navigateEffect.serviceListRequest.targetStation.name)
            assertEquals(null, navigateEffect.serviceListRequest.filterStation)
        }
    }

    @Test
    fun `searchTrains with target station and no filter should post NavigateToServiceList effect for ARRIVALS`() = runTest {
        // Given
        every { timeProvider.currentDateMillis() } returns TEST_CURRENT_DATE_MILLIS
        coEvery { recentSearchesRepository.getRecentSearches() } returns flowOf(emptyList())
        viewModel = HomeViewModel(recentSearchesRepository, timeProvider)
        val targetStation = createTestStation(STATION_NAME_LEEDS, STATION_CRS_LEEDS)

        // When
        viewModel.test(this) {
            // Update query type to ARRIVALS
            containerHost.updateQueryType(QueryType.ARRIVALS)
            awaitState()

            // Select target station
            containerHost.stationSelected(StationType.TARGET, targetStation)
            awaitState()

            // Search trains
            containerHost.searchTrains()

            // Then
            val effect = awaitSideEffect()
            assertTrue(effect is HomeEffect.NavigateToServiceList)
            val navigateEffect = effect as HomeEffect.NavigateToServiceList
            assertEquals(ServiceListType.ARRIVALS, navigateEffect.serviceListRequest.serviceListType)
        }
    }

    @Test
    fun `searchTrains with target and filter station should include filter in request`() = runTest {
        // Given
        every { timeProvider.currentDateMillis() } returns TEST_CURRENT_DATE_MILLIS
        coEvery { recentSearchesRepository.getRecentSearches() } returns flowOf(emptyList())
        viewModel = HomeViewModel(recentSearchesRepository, timeProvider)
        val targetStation = createTestStation(STATION_NAME_LEEDS, STATION_CRS_LEEDS)
        val filterStation = createTestStation(STATION_NAME_MANCHESTER, STATION_CRS_MANCHESTER)

        // When
        viewModel.test(this) {
            // Select stations
            containerHost.stationSelected(StationType.TARGET, targetStation)
            awaitState()
            containerHost.stationSelected(StationType.FILTER, filterStation)
            awaitState()

            // Search trains
            containerHost.searchTrains()

            // Then
            val effect = awaitSideEffect()
            assertTrue(effect is HomeEffect.NavigateToServiceList)
            val navigateEffect = effect as HomeEffect.NavigateToServiceList
            assertEquals(ServiceListType.DEPARTURES, navigateEffect.serviceListRequest.serviceListType)
            assertEquals(STATION_CRS_LEEDS, navigateEffect.serviceListRequest.targetStation.crs)
            assertEquals(STATION_NAME_LEEDS, navigateEffect.serviceListRequest.targetStation.name)
            assertEquals(STATION_CRS_MANCHESTER, navigateEffect.serviceListRequest.filterStation?.crs)
            assertEquals(STATION_NAME_MANCHESTER, navigateEffect.serviceListRequest.filterStation?.name)
        }
    }

    @Test
    fun `searchTrains with custom time should include AtTime in request`() = runTest {
        // Given
        every { timeProvider.currentDateMillis() } returns TEST_CURRENT_DATE_MILLIS
        every { timeProvider.convertMillisToDate(TEST_DATE_MILLIS) } returns TEST_LOCAL_DATE
        coEvery { recentSearchesRepository.getRecentSearches() } returns flowOf(emptyList())
        viewModel = HomeViewModel(recentSearchesRepository, timeProvider)
        val targetStation = createTestStation(STATION_NAME_LEEDS, STATION_CRS_LEEDS)

        // When
        viewModel.test(this) {
            // Select target station and time
            containerHost.stationSelected(StationType.TARGET, targetStation)
            awaitState()
            containerHost.processSelectedDateTime(TEST_DATE_MILLIS, TEST_HOUR, TEST_MINUTE)
            awaitState()

            // Search trains
            containerHost.searchTrains()

            // Then
            val effect = awaitSideEffect()
            assertTrue(effect is HomeEffect.NavigateToServiceList)
            val navigateEffect = effect as HomeEffect.NavigateToServiceList
            assertTrue(navigateEffect.serviceListRequest.requestTime is RequestTime.AtTime)
            val atTime = navigateEffect.serviceListRequest.requestTime as RequestTime.AtTime
            assertEquals(EXPECTED_YEAR, atTime.year)
            assertEquals(EXPECTED_MONTH, atTime.month)
            assertEquals(EXPECTED_DAY, atTime.day)
            assertEquals(EXPECTED_HOURS_PADDED, atTime.hours)
            assertEquals(EXPECTED_MINS_PADDED, atTime.mins)
        }
    }

    @Test
    fun `multiple station selections should only keep the most recent selection`() = runTest {
        // Given
        every { timeProvider.currentDateMillis() } returns TEST_CURRENT_DATE_MILLIS
        coEvery { recentSearchesRepository.getRecentSearches() } returns flowOf(emptyList())
        viewModel = HomeViewModel(recentSearchesRepository, timeProvider)
        val firstStation = createTestStation(STATION_NAME_LEEDS, STATION_CRS_LEEDS)
        val secondStation = createTestStation(STATION_NAME_MANCHESTER, STATION_CRS_MANCHESTER)

        // When
        viewModel.test(this) {
            // Select first station
            containerHost.stationSelected(StationType.TARGET, firstStation)
            awaitState()

            // Select second station (should replace first)
            containerHost.stationSelected(StationType.TARGET, secondStation)

            // Then
            val state = awaitState()
            assertTrue(state.targetStationSelection is StationSelection.Selected)
            val selected = (state.targetStationSelection as StationSelection.Selected).station
            assertEquals(STATION_NAME_MANCHESTER, selected.name)
            assertEquals(STATION_CRS_MANCHESTER, selected.crs)
        }
    }

    @Test
    fun `clearing station after selection should allow reselection`() = runTest {
        // Given
        every { timeProvider.currentDateMillis() } returns TEST_CURRENT_DATE_MILLIS
        coEvery { recentSearchesRepository.getRecentSearches() } returns flowOf(emptyList())
        viewModel = HomeViewModel(recentSearchesRepository, timeProvider)
        val station = createTestStation(STATION_NAME_LEEDS, STATION_CRS_LEEDS)

        // When
        viewModel.test(this) {
            // Select station
            containerHost.stationSelected(StationType.TARGET, station)
            awaitState()

            // Clear station
            containerHost.clearTargetStation()
            awaitState()

            // Select again
            containerHost.stationSelected(StationType.TARGET, station)

            // Then
            val state = awaitState()
            assertTrue(state.targetStationSelection is StationSelection.Selected)
            assertEquals(station, (state.targetStationSelection as StationSelection.Selected).station)
        }
    }

    @Test
    fun `clearAllRecentSearches should clear all recent searches successfully`() = runTest {
        // Given
        every { timeProvider.currentDateMillis() } returns TEST_CURRENT_DATE_MILLIS
        coEvery { recentSearchesRepository.getRecentSearches() } returns flowOf(emptyList())
        coEvery { recentSearchesRepository.deleteAllRecentSearches() } returns Ok(Unit)
        viewModel = HomeViewModel(recentSearchesRepository, timeProvider)

        // When
        viewModel.test(this) {
            containerHost.clearAllRecentSearches()
        }

        // Then
        coVerify { recentSearchesRepository.deleteAllRecentSearches() }
    }

    @Test
    fun `clearAllRecentSearches should post ShowFailedToClearRecentSearchesError on failure`() = runTest {
        // Given
        every { timeProvider.currentDateMillis() } returns TEST_CURRENT_DATE_MILLIS
        coEvery { recentSearchesRepository.getRecentSearches() } returns flowOf(emptyList())
        coEvery { recentSearchesRepository.deleteAllRecentSearches() } returns Err(LocalDataError.Unknown)
        viewModel = HomeViewModel(recentSearchesRepository, timeProvider)

        // When
        viewModel.test(this) {
            containerHost.clearAllRecentSearches()

            // Then
            expectSideEffect(HomeEffect.ShowFailedToClearRecentSearchesError)
        }
    }

    // Helper function to create test station
    private fun createTestStation(name: String, crs: String): Station {
        return Station(name = name, crs = crs)
    }

    private fun createTestTrainStation(name: String, crs: String): TrainStation {
        return TrainStation(name = name, crs = crs)
    }

    private fun createStationListRequest(): ServiceListRequest {
        return ServiceListRequest(
            serviceListType = ServiceListType.DEPARTURES,
            requestTime = RequestTime.Now,
            targetStation = createTestTrainStation(STATION_NAME_LEEDS, STATION_CRS_LEEDS),
            filterStation = createTestTrainStation(STATION_NAME_MANCHESTER, STATION_CRS_MANCHESTER)
        )
    }

    // Test constants
    private companion object {
        // Station test data
        const val STATION_NAME_LEEDS = "Leeds"
        const val STATION_CRS_LEEDS = "LDS"
        const val STATION_NAME_MANCHESTER = "Manchester Piccadilly"
        const val STATION_CRS_MANCHESTER = "MAN"

        // Time test data
        const val TEST_CURRENT_DATE_MILLIS = 1672531200000L // 2023-01-01 00:00:00
        const val TEST_DATE_MILLIS = 1704067200000L // 2024-01-01 00:00:00
        val TEST_LOCAL_DATE: LocalDate = LocalDate.of(2024, 1, 1)
        const val TEST_HOUR = 14
        const val TEST_MINUTE = 30
        const val SINGLE_DIGIT_HOUR = 9
        const val SINGLE_DIGIT_MINUTE = 5

        // Expected formatted values
        const val EXPECTED_YEAR = "2024"
        const val EXPECTED_MONTH = "1"
        const val EXPECTED_DAY = "1"
        const val EXPECTED_HOURS_PADDED = "14"
        const val EXPECTED_MINS_PADDED = "30"
        const val EXPECTED_SINGLE_HOUR_PADDED = "09"
        const val EXPECTED_SINGLE_MIN_PADDED = "05"
    }
}
