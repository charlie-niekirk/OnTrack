package me.cniekirk.ontrack.feature.stationsearch

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import me.cniekirk.ontrack.core.domain.model.Station
import me.cniekirk.ontrack.core.domain.model.error.NetworkError
import me.cniekirk.ontrack.core.domain.repository.StationsRepository
import me.cniekirk.ontrack.core.navigation.StationType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.orbitmvi.orbit.test.test

@OptIn(ExperimentalCoroutinesApi::class)
class StationSearchViewModelTest {

    private val repository = mockk<StationsRepository>()
    private lateinit var viewModel: StationSearchViewModel

    @Test
    fun `getAllStations success should update state with stations and set loading false`() = runTest {
        // Given
        val stations = createTestStations()
        coEvery { repository.getStations(any()) } returns Ok(stations)

        // When
        viewModel = StationSearchViewModel(STATION_TYPE_TARGET, repository)

        // Then
        viewModel.test(this) {
            runOnCreate()

            val finalState = awaitState()
            assertFalse(finalState.isLoading)
            assertEquals(EXPECTED_SIZE_ALL_STATIONS, finalState.stations.size)
            assertEquals(stations, finalState.stations)
        }
    }

    @Test
    fun `getAllStations failure should set loading false and post error effect`() = runTest {
        // Given
        val error = NetworkError.NetworkFailure(Exception("Network error"))
        coEvery { repository.getStations(any()) } returns Err(error)

        // When
        viewModel = StationSearchViewModel(STATION_TYPE_TARGET, repository)

        // Then
        viewModel.test(this) {
            runOnCreate()

            val finalState = awaitState()
            assertFalse(finalState.isLoading)
            assertEquals(EXPECTED_SIZE_EMPTY, finalState.stations.size)

            val sideEffect = awaitSideEffect()
            assertTrue(sideEffect is StationSearchEffect.ShowError)
            assertEquals(ERROR_MESSAGE_STATION_FETCH, (sideEffect as StationSearchEffect.ShowError).message)
        }
    }

    @Test
    fun `searchStations should filter by CRS code case-insensitive`() = runTest {
        // Given
        val stations = createTestStations()
        coEvery { repository.getStations(any()) } returns Ok(stations)
        viewModel = StationSearchViewModel(STATION_TYPE_TARGET, repository)

        // When
        viewModel.test(this) {
            runOnCreate()
            awaitState() // Consume the state after loading

            // Search by CRS code
            containerHost.searchStations(SEARCH_QUERY_LDS)

            // Then
            val searchState = awaitState()
            assertEquals(SEARCH_QUERY_LDS, searchState.searchQuery)

            val filteredState = awaitState()
            assertEquals(EXPECTED_SIZE_ONE_STATION, filteredState.stations.size)
            assertEquals(STATION_CRS_LEEDS, filteredState.stations[0].crs)
            assertEquals(STATION_NAME_LEEDS, filteredState.stations[0].name)
        }
    }

    @Test
    fun `searchStations should filter by station name case-insensitive`() = runTest {
        // Given
        val stations = createTestStations()
        coEvery { repository.getStations(any()) } returns Ok(stations)
        viewModel = StationSearchViewModel(STATION_TYPE_TARGET, repository)

        // When
        viewModel.test(this) {
            runOnCreate()
            awaitState() // Consume the state after loading

            // Search by station name
            containerHost.searchStations(SEARCH_QUERY_LEEDS)

            // Then
            val searchState = awaitState()
            assertEquals(SEARCH_QUERY_LEEDS, searchState.searchQuery)

            val filteredState = awaitState()
            assertEquals(EXPECTED_SIZE_ONE_STATION, filteredState.stations.size)
            assertEquals(STATION_NAME_LEEDS, filteredState.stations[0].name)
        }
    }

    @Test
    fun `searchStations with lowercase query should match uppercase station data`() = runTest {
        // Given
        val stations = createTestStations()
        coEvery { repository.getStations(any()) } returns Ok(stations)
        viewModel = StationSearchViewModel(STATION_TYPE_TARGET, repository)

        // When
        viewModel.test(this) {
            runOnCreate()
            awaitState()

            containerHost.searchStations(SEARCH_QUERY_LOWERCASE)

            // Then
            val searchState = awaitState()
            assertEquals(SEARCH_QUERY_LOWERCASE, searchState.searchQuery)

            val filteredState = awaitState()
            assertEquals(EXPECTED_SIZE_ONE_STATION, filteredState.stations.size)
            assertEquals(STATION_NAME_LEEDS, filteredState.stations[0].name)
        }
    }

    @Test
    fun `searchStations with uppercase query should match lowercase station data`() = runTest {
        // Given
        val stations = createTestStations()
        coEvery { repository.getStations(any()) } returns Ok(stations)
        viewModel = StationSearchViewModel(STATION_TYPE_TARGET, repository)

        // When
        viewModel.test(this) {
            runOnCreate()
            awaitState()

            containerHost.searchStations(SEARCH_QUERY_UPPERCASE)

            // Then
            val searchState = awaitState()
            assertEquals(SEARCH_QUERY_UPPERCASE, searchState.searchQuery)

            val filteredState = awaitState()
            assertEquals(EXPECTED_SIZE_ONE_STATION, filteredState.stations.size)
        }
    }

    @Test
    fun `searchStations with partial match should return matching stations`() = runTest {
        // Given
        val stations = createTestStations()
        coEvery { repository.getStations(any()) } returns Ok(stations)
        viewModel = StationSearchViewModel(STATION_TYPE_TARGET, repository)

        // When
        viewModel.test(this) {
            runOnCreate()
            awaitState()

            // Partial match for "Man" should match "Manchester"
            containerHost.searchStations(SEARCH_QUERY_PARTIAL)

            // Then
            val searchState = awaitState()
            assertEquals(SEARCH_QUERY_PARTIAL, searchState.searchQuery)

            val filteredState = awaitState()
            assertEquals(EXPECTED_SIZE_ONE_STATION, filteredState.stations.size)
            assertEquals(STATION_NAME_MANCHESTER, filteredState.stations[0].name)
        }
    }

    @Test
    fun `searchStations should match stations containing London in name`() = runTest {
        // Given
        val stations = createTestStations()
        coEvery { repository.getStations(any()) } returns Ok(stations)
        viewModel = StationSearchViewModel(STATION_TYPE_TARGET, repository)

        // When
        viewModel.test(this) {
            runOnCreate()
            awaitState()

            containerHost.searchStations(SEARCH_QUERY_LONDON)

            // Then
            val searchState = awaitState()
            assertEquals(SEARCH_QUERY_LONDON, searchState.searchQuery)

            val filteredState = awaitState()
            assertEquals(EXPECTED_SIZE_ONE_STATION, filteredState.stations.size)
            assertTrue(filteredState.stations[0].name.contains(SEARCH_QUERY_LONDON))
        }
    }

    @Test
    fun `searchStations with no matches should return empty list`() = runTest {
        // Given
        val stations = createTestStations()
        coEvery { repository.getStations(any()) } returns Ok(stations)
        viewModel = StationSearchViewModel(STATION_TYPE_TARGET, repository)

        // When
        viewModel.test(this) {
            runOnCreate()
            awaitState()

            containerHost.searchStations(SEARCH_QUERY_NO_MATCH)

            // Then
            val searchState = awaitState()
            assertEquals(SEARCH_QUERY_NO_MATCH, searchState.searchQuery)

            val filteredState = awaitState()
            assertEquals(EXPECTED_SIZE_EMPTY, filteredState.stations.size)
        }
    }

    @Test
    fun `searchStations with empty query should return all stations`() = runTest {
        // Given
        val stations = createTestStations()
        coEvery { repository.getStations(any()) } returns Ok(stations)
        viewModel = StationSearchViewModel(STATION_TYPE_TARGET, repository)

        // When
        viewModel.test(this) {
            runOnCreate()
            awaitState()

            containerHost.searchStations(SEARCH_QUERY_NO_MATCH)
            awaitState()
            awaitState()

            containerHost.searchStations(SEARCH_QUERY_EMPTY)

            // Then
            val searchState = awaitState()
            assertEquals(SEARCH_QUERY_EMPTY, searchState.searchQuery)

            val filteredState = awaitState()
            assertEquals(EXPECTED_SIZE_ALL_STATIONS, filteredState.stations.size)
        }
    }

    @Test
    fun `searchStations should update search query in state`() = runTest {
        // Given
        val stations = createTestStations()
        coEvery { repository.getStations(any()) } returns Ok(stations)
        viewModel = StationSearchViewModel(STATION_TYPE_TARGET, repository)

        // When
        viewModel.test(this) {
            runOnCreate()
            awaitState()

            containerHost.searchStations(SEARCH_QUERY_LEEDS)

            // Then
            val searchState = awaitState()
            assertEquals(SEARCH_QUERY_LEEDS, searchState.searchQuery)
            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `multiple consecutive searches should update state correctly`() = runTest {
        // Given
        val stations = createTestStations()
        coEvery { repository.getStations(any()) } returns Ok(stations)
        viewModel = StationSearchViewModel(STATION_TYPE_TARGET, repository)

        // When
        viewModel.test(this) {
            runOnCreate()
            awaitState()

            // First search
            containerHost.searchStations(SEARCH_QUERY_LEEDS)
            awaitState()
            val firstSearchState = awaitState()
            assertEquals(EXPECTED_SIZE_ONE_STATION, firstSearchState.stations.size)

            // Second search
            containerHost.searchStations(SEARCH_QUERY_LONDON)
            awaitState()
            val secondSearchState = awaitState()
            assertEquals(EXPECTED_SIZE_ONE_STATION, secondSearchState.stations.size)
            assertEquals(SEARCH_QUERY_LONDON, secondSearchState.searchQuery)

            // Third search with no results
            containerHost.searchStations(SEARCH_QUERY_NO_MATCH)
            awaitState()
            val thirdSearchState = awaitState()
            assertEquals(EXPECTED_SIZE_EMPTY, thirdSearchState.stations.size)
        }
    }

    @Test
    fun `searchStations maintains in-memory cache across searches`() = runTest {
        // Given
        val stations = createTestStations()
        coEvery { repository.getStations(any()) } returns Ok(stations)
        viewModel = StationSearchViewModel(STATION_TYPE_TARGET, repository)

        // When
        viewModel.test(this) {
            runOnCreate()
            awaitState()

            // First search to narrow down
            containerHost.searchStations(SEARCH_QUERY_LEEDS)
            awaitState()
            awaitState()

            // Second search with empty query should return all cached stations
            containerHost.searchStations(SEARCH_QUERY_EMPTY)
            awaitState()
            val finalState = awaitState()

            // Then
            assertEquals(EXPECTED_SIZE_ALL_STATIONS, finalState.stations.size)
        }
    }

    // Helper function to create test stations
    private fun createTestStations(): List<Station> {
        return listOf(
            Station(name = STATION_NAME_LEEDS, crs = STATION_CRS_LEEDS),
            Station(name = STATION_NAME_MANCHESTER, crs = STATION_CRS_MANCHESTER),
            Station(name = STATION_NAME_LONDON, crs = STATION_CRS_LONDON),
            Station(name = STATION_NAME_YORK, crs = STATION_CRS_YORK)
        )
    }

    // Test constants
    private companion object {
        // Station test data
        const val STATION_CRS_LEEDS = "LDS"
        const val STATION_NAME_LEEDS = "Leeds"
        const val STATION_CRS_MANCHESTER = "MAN"
        const val STATION_NAME_MANCHESTER = "Manchester Piccadilly"
        const val STATION_CRS_LONDON = "KGX"
        const val STATION_NAME_LONDON = "London Kings Cross"
        const val STATION_CRS_YORK = "YRK"
        const val STATION_NAME_YORK = "York"

        // Search query test data
        const val SEARCH_QUERY_LEEDS = "Leeds"
        const val SEARCH_QUERY_LDS = "LDS"
        const val SEARCH_QUERY_LONDON = "London"
        const val SEARCH_QUERY_LOWERCASE = "leeds"
        const val SEARCH_QUERY_UPPERCASE = "LEEDS"
        const val SEARCH_QUERY_PARTIAL = "Man"
        const val SEARCH_QUERY_NO_MATCH = "XYZ"
        const val SEARCH_QUERY_EMPTY = ""

        // Station type test data
        val STATION_TYPE_TARGET = StationType.TARGET

        // Error message
        val ERROR_MESSAGE_STATION_FETCH = R.string.station_fetch_error

        // Expected list sizes
        const val EXPECTED_SIZE_ALL_STATIONS = 4
        const val EXPECTED_SIZE_ONE_STATION = 1
        const val EXPECTED_SIZE_EMPTY = 0
    }
}
