package me.cniekirk.ontrack.feature.home

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DatePicker
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
import androidx.compose.ui.unit.dp
import me.cniekirk.ontrack.core.compose.theme.OnTrackTheme
import me.cniekirk.ontrack.core.navigation.RequestTime
import me.cniekirk.ontrack.core.navigation.ServiceListRequest
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
        onSearchClicked = viewModel::searchTrains
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
    onSearchClicked: () -> Unit
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun HomeScreenPreview() {
    val state = HomeState(currentDateMillis = 0L)

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
                onSearchClicked = {}
            )
        }
    }
}