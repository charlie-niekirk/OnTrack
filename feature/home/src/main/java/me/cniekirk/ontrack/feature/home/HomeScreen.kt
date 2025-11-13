package me.cniekirk.ontrack.feature.home

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import me.cniekirk.ontrack.core.compose.theme.OnTrackTheme
import me.cniekirk.ontrack.core.domain.model.arguments.RequestTime
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceListRequest
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceListType
import me.cniekirk.ontrack.core.domain.model.arguments.TrainStation
import me.cniekirk.ontrack.core.navigation.StationType
import me.cniekirk.ontrack.feature.home.components.DepartingArrivingButtonGroup
import me.cniekirk.ontrack.feature.home.components.StationCard
import me.cniekirk.ontrack.feature.home.components.TimePickerDialog
import me.cniekirk.ontrack.feature.home.state.HomeEffect
import me.cniekirk.ontrack.feature.home.state.HomeState
import me.cniekirk.ontrack.feature.home.state.QueryType
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    navigateToStationSelection: (StationType) -> Unit,
    navigateToServiceList: (ServiceListRequest) -> Unit
) {
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is HomeEffect.NavigateToServiceList -> {
                navigateToServiceList(sideEffect.serviceListRequest)
            }
            HomeEffect.ShowNoStationSelectedError -> {

            }
            HomeEffect.ShowFailedToFetchRecentSearchesError -> {

            }
        }
    }

    HomeScreen(
        state = state,
        onQueryTypeChanged = viewModel::updateQueryType,
        onTargetStationClicked = { navigateToStationSelection(StationType.TARGET) },
        onFilterStationClicked = { navigateToStationSelection(StationType.FILTER) },
        onClearTargetStationClicked = viewModel::clearTargetStation,
        onClearFilterStationClicked = viewModel::clearFilterStation,
        onDateTimeSet = viewModel::processSelectedDateTime,
        onResetDateTimeClicked = viewModel::resetDateTime,
        onSearchClicked = viewModel::searchTrains,
        onRecentSearchClicked = { navigateToServiceList(it) },
        onClearAllRecentSearchesClicked = viewModel::clearAllRecentSearches
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    state: HomeState,
    onQueryTypeChanged: (QueryType) -> Unit,
    onTargetStationClicked: () -> Unit,
    onFilterStationClicked: () -> Unit,
    onClearTargetStationClicked: () -> Unit,
    onClearFilterStationClicked: () -> Unit,
    onDateTimeSet: (Long, Int, Int) -> Unit,
    onResetDateTimeClicked: () -> Unit,
    onSearchClicked: () -> Unit,
    onRecentSearchClicked: (ServiceListRequest) -> Unit,
    onClearAllRecentSearchesClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            text = stringResource(R.string.train_times_title),
            style = MaterialTheme.typography.headlineMedium
        )

        DepartingArrivingButtonGroup(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            queryType = state.queryType,
            onQueryTypeChanged = { onQueryTypeChanged(it) }
        )

        StationCard(
            stationSelection = state.targetStationSelection,
            placeholder = getPlaceholderText(isFilter = false, queryType = state.queryType),
            onClick = { onTargetStationClicked() },
            onClearSelectionClick = { onClearTargetStationClicked() }
        )

        StationCard(
            modifier = Modifier.padding(top = 16.dp),
            stationSelection = state.filterStationSelection,
            placeholder = getPlaceholderText(isFilter = true, queryType = state.queryType),
            onClick = { onFilterStationClicked() },
            onClearSelectionClick = { onClearFilterStationClicked() }
        )

        var tempDateMillis by remember { mutableLongStateOf(0L) }
        var showDatePicker by rememberSaveable { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.currentDateMillis)

        var showTimePicker by remember { mutableStateOf(false) }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = {
                    showDatePicker = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            tempDateMillis = datePickerState.selectedDateMillis ?: 0L
                            showTimePicker = true
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.date_picker_confirm),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(
                            text = stringResource(R.string.date_picker_cancel),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showTimePicker) {
            val currentTime = Calendar.getInstance()

            val timePickerState = rememberTimePickerState(
                initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                initialMinute = currentTime.get(Calendar.MINUTE),
                is24Hour = true,
            )

            TimePickerDialog(
                onDismiss = {
                    showTimePicker = false
                    tempDateMillis = 0L
                },
                onConfirm = {
                    onDateTimeSet(
                        tempDateMillis,
                        timePickerState.hour,
                        timePickerState.minute
                    )
                    tempDateMillis = 0L
                    showTimePicker = false
                    showDatePicker = false
                }
            ) {
                TimePicker(state = timePickerState)
            }
        }

        Row(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InputChip(
                selected = false,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null
                    )
                },
                label = {
                    val timeString = when (val dateTime = state.requestTime) {
                        is RequestTime.AtTime -> {
                            stringResource(
                                R.string.set_departure_time,
                                dateTime.day,
                                dateTime.month,
                                dateTime.year,
                                dateTime.hours,
                                dateTime.mins
                            )
                        }
                        is RequestTime.Now -> stringResource(R.string.now_departure_time)
                    }
                    Text(
                        text = timeString,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                trailingIcon = {
                    val icon = when (state.requestTime) {
                        is RequestTime.AtTime -> Icons.Default.Clear
                        RequestTime.Now -> Icons.Default.KeyboardArrowDown
                    }
                    Icon(
                        modifier = Modifier.clickable {
                            when (state.requestTime) {
                                is RequestTime.AtTime -> {
                                    onResetDateTimeClicked()
                                }
                                RequestTime.Now -> {
                                    showDatePicker = true
                                }
                            }
                        },
                        imageVector = icon,
                        contentDescription = null
                    )
                },
                onClick = {
                    // Open date/time picker
                    showDatePicker = true
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            FilledTonalButton(
                onClick = { onSearchClicked() }
            ) {
                Text(
                    text = stringResource(R.string.button_search)
                )
            }
        }

        RecentSearchesSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            recentSearches = state.recentSearches,
            onRecentSearchClicked = { onRecentSearchClicked(it) },
            onClearAllRecentSearchesClicked = { onClearAllRecentSearchesClicked() }
        )
    }
}

@Composable
private fun RecentSearchesSection(
    modifier: Modifier = Modifier,
    recentSearches: List<ServiceListRequest>,
    onRecentSearchClicked: (ServiceListRequest) -> Unit,
    onClearAllRecentSearchesClicked: () -> Unit
) {
    Column(modifier = modifier) {
        if (recentSearches.isNotEmpty()) {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.recent_searches_title),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.weight(1f))

                TextButton(
                    onClick = { onClearAllRecentSearchesClicked() }
                ) {
                    Text(
                        text = stringResource(R.string.clear_recent_searches),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentSearches) { search ->
                    RecentSearchItem(
                        search = search,
                        onClick = { onRecentSearchClicked(search) }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_recent_searches),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RecentSearchItem(
    modifier: Modifier = Modifier,
    search: ServiceListRequest,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = search.targetStation.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = when (search.serviceListType) {
                        ServiceListType.DEPARTURES -> stringResource(R.string.departures_title)
                        ServiceListType.ARRIVALS -> stringResource(R.string.arrivals_title)
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            search.filterStation?.let { filterStation ->
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = stringResource(R.string.recent_search_filter, filterStation.name),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = when (val time = search.requestTime) {
                    is RequestTime.Now -> stringResource(R.string.now_departure_time)
                    is RequestTime.AtTime -> stringResource(
                        R.string.set_departure_time,
                        time.day,
                        time.month,
                        time.year,
                        time.hours,
                        time.mins
                    )
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@StringRes
private fun getPlaceholderText(isFilter: Boolean, queryType: QueryType): Int {
    return when (queryType) {
        QueryType.DEPARTURES -> {
            if (isFilter) {
                R.string.empty_departing_filter_station
            } else {
                R.string.empty_departing_station
            }
        }
        QueryType.ARRIVALS -> {
            if (isFilter) {
                R.string.empty_arriving_filter_station
            } else {
                R.string.empty_arriving_station
            }
        }
    }
}

private class HomeStatePreviewParameterProvider : PreviewParameterProvider<HomeState> {
    override val values: Sequence<HomeState> = sequenceOf(
        // Empty recent searches
        HomeState(currentDateMillis = 0L),
        // Populated recent searches
        HomeState(
            currentDateMillis = 0L,
            recentSearches = listOf(
                ServiceListRequest(
                    serviceListType = ServiceListType.DEPARTURES,
                    requestTime = RequestTime.Now,
                    targetStation = TrainStation(crs = "VIC", name = "London Victoria"),
                    filterStation = TrainStation(crs = "BRI", name = "Brighton")
                ),
                ServiceListRequest(
                    serviceListType = ServiceListType.ARRIVALS,
                    requestTime = RequestTime.AtTime(
                        year = "2024",
                        month = "03",
                        day = "15",
                        hours = "14",
                        mins = "30"
                    ),
                    targetStation = TrainStation(crs = "PAD", name = "London Paddington"),
                    filterStation = null
                )
            )
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun HomeScreenPreview(
    @PreviewParameter(HomeStatePreviewParameterProvider::class) state: HomeState
) {
    OnTrackTheme {
        Surface {
            HomeScreen(
                state = state,
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
}

@Preview
@Composable
private fun RecentSearchItemPreview() {
    val sampleSearch = ServiceListRequest(
        serviceListType = ServiceListType.DEPARTURES,
        requestTime = RequestTime.Now,
        targetStation = TrainStation(crs = "VIC", name = "London Victoria"),
        filterStation = TrainStation(crs = "BRI", name = "Brighton")
    )

    OnTrackTheme {
        Surface {
            RecentSearchItem(
                search = sampleSearch,
                onClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun RecentSearchItemWithTimePreview() {
    val sampleSearch = ServiceListRequest(
        serviceListType = ServiceListType.ARRIVALS,
        requestTime = RequestTime.AtTime(
            year = "2024",
            month = "03",
            day = "15",
            hours = "14",
            mins = "30"
        ),
        targetStation = TrainStation(crs = "PAD", name = "London Paddington"),
        filterStation = null
    )

    OnTrackTheme {
        Surface {
            RecentSearchItem(
                search = sampleSearch,
                onClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun RecentSearchesSectionWithDataPreview() {
    val sampleSearches = listOf(
        ServiceListRequest(
            serviceListType = ServiceListType.DEPARTURES,
            requestTime = RequestTime.Now,
            targetStation = TrainStation(crs = "VIC", name = "London Victoria"),
            filterStation = TrainStation(crs = "BRI", name = "Brighton")
        ),
        ServiceListRequest(
            serviceListType = ServiceListType.ARRIVALS,
            requestTime = RequestTime.AtTime(
                year = "2024",
                month = "03",
                day = "15",
                hours = "14",
                mins = "30"
            ),
            targetStation = TrainStation(crs = "PAD", name = "London Paddington"),
            filterStation = null
        )
    )

    OnTrackTheme {
        Surface {
            RecentSearchesSection(
                recentSearches = sampleSearches,
                onRecentSearchClicked = {},
                onClearAllRecentSearchesClicked = {}
            )
        }
    }
}

@Preview
@Composable
private fun RecentSearchesSectionEmptyPreview() {
    OnTrackTheme {
        Surface {
            RecentSearchesSection(
                recentSearches = emptyList(),
                onRecentSearchClicked = {},
                onClearAllRecentSearchesClicked = {}
            )
        }
    }
}