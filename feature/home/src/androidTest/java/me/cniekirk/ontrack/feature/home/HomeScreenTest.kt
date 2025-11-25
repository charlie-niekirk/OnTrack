package me.cniekirk.ontrack.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import me.cniekirk.ontrack.core.domain.model.arguments.RequestTime
import me.cniekirk.ontrack.feature.home.state.HomeState
import me.cniekirk.ontrack.feature.home.state.QueryType
import me.cniekirk.ontrack.feature.home.state.StationSelection
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun noRecentSearchesText_whenRecentSearchesEmpty_exists() {
        composeTestRule.setContent {
            Box {
                HomeScreen(
                    state = HomeState(
                        queryType = QueryType.DEPARTURES,
                        targetStationSelection = StationSelection.None,
                        filterStationSelection = StationSelection.None,
                        requestTime = RequestTime.Now,
                        currentDateMillis = 0L,
                        recentSearches = emptyList()
                    ),
                    onQueryTypeChanged = {},
                    onTargetStationClicked = {},
                    onFilterStationClicked = {},
                    onClearTargetStationClicked = {},
                    onClearFilterStationClicked = {},
                    onDateTimeSet = { _, _, _ -> },
                    onResetDateTimeClicked = {},
                    onSearchClicked = {},
                    onRecentSearchClicked = {},
                    onClearAllRecentSearchesClicked = {}
                )
            }
        }

        composeTestRule
            .onNodeWithTag("no_recent_searches")
            .assertExists()
    }
}