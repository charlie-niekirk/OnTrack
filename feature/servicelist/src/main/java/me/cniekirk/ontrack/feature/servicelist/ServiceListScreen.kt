package me.cniekirk.ontrack.feature.servicelist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.cniekirk.ontrack.core.domain.model.services.Platform
import me.cniekirk.ontrack.core.domain.model.services.ServiceLocation
import me.cniekirk.ontrack.core.domain.model.services.TimeStatus
import me.cniekirk.ontrack.core.domain.model.services.TrainService
import me.cniekirk.ontrack.core.navigation.ServiceListType
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ServiceListRoute(viewModel: ServiceListViewModel) {
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            else -> {}
        }
    }

    ServiceListScreen(state = state)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ServiceListScreen(state: ServiceListState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val prefixText = when (state.serviceListType) {
            ServiceListType.DEPARTURES -> stringResource(R.string.departures)
            ServiceListType.ARRIVALS -> stringResource(R.string.arrivals)
        }

        Text(
            modifier = Modifier
                .align(Alignment.Start)
                .padding(
                    top = 16.dp,
                    start = 16.dp
                ),
            text = prefixText,
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier
                .align(Alignment.Start)
                .padding(
                    top = 4.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val filter = state.filterStation
            val stationTitleText = if (filter != null) {
                when (state.serviceListType) {
                    ServiceListType.DEPARTURES -> stringResource(R.string.to, state.targetStation, filter)
                    ServiceListType.ARRIVALS -> stringResource(R.string.from, state.targetStation, filter)
                }
            } else state.targetStation

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stationTitleText,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularWavyProgressIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxSize()
                ) {
                    items(state.trainServiceList) { trainService ->
                        TrainServiceListItem(
                            trainService = trainService,
                            serviceListType = state.serviceListType
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun TrainServiceListItem(
    trainService: TrainService,
    serviceListType: ServiceListType,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val scheduledTime = when (val time = trainService.timeStatus) {
            is TimeStatus.Cancelled -> time.scheduledDepartureTime
            is TimeStatus.Delayed -> time.scheduledTime
            is TimeStatus.Departed -> time.scheduledDepartureTime
            is TimeStatus.Arrived -> time.scheduledArrivalTime
            is TimeStatus.OnTime -> time.scheduledTime
            TimeStatus.Unknown -> ""
        }
        Text(
            text = scheduledTime,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        Column(
            modifier = Modifier
                .weight(1f) // Take available space but allow wrapping
                .padding(start = 16.dp)
        ) {
            val stationName = when (serviceListType) {
                ServiceListType.DEPARTURES -> trainService.destination
                ServiceListType.ARRIVALS -> trainService.origin
            }
            Text(
                text = stationName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2, // Allow wrapping for long text
                overflow = TextOverflow.Ellipsis
            )

            val serviceStatusText = getServiceStatusText(trainService)
            Text(
                modifier = Modifier.padding(vertical = 2.dp),
                text = serviceStatusText,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                modifier = Modifier.padding(top = 2.dp),
                text = trainService.trainOperatingCompany,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Remove Spacer, as it's no longer needed with weight on Column
        trainService.platform?.let {
            PlatformText(
                modifier = Modifier.align(Alignment.CenterVertically),
                platform = it,
            )
        }
    }
}

@Composable
private fun getServiceStatusText(trainService: TrainService): String {
    val serviceLocation = trainService.serviceLocation
    return if (serviceLocation != null) {
        when (serviceLocation) {
            ServiceLocation.APPROACHING_STATION -> stringResource(R.string.service_location_approaching_station)
            ServiceLocation.APPROACHING_PLATFORM -> stringResource(R.string.service_location_approaching_platform)
            ServiceLocation.AT_PLATFORM -> stringResource(R.string.service_location_at_platform)
            ServiceLocation.PREPARING_DEPARTURE -> stringResource(R.string.service_location_preparing_departure)
            ServiceLocation.READY_TO_DEPART -> stringResource(R.string.service_location_ready_to_depart)
        }
    } else {
        when (val timeStatus = trainService.timeStatus) {
            is TimeStatus.Cancelled -> stringResource(R.string.cancelled_time_status, timeStatus.reason)
            is TimeStatus.Delayed -> stringResource(R.string.delayed_time_status, timeStatus.delayInMinutes)
            is TimeStatus.Departed -> {
                if (timeStatus.delayInMinutes > 0) {
                    stringResource(R.string.departed_delayed_time_status, timeStatus.delayInMinutes)
                } else {
                    stringResource(R.string.departed_on_time_time_status)
                }
            }
            is TimeStatus.Arrived -> {
                if (timeStatus.delayInMinutes > 0) {
                    stringResource(R.string.arrived_delayed_time_status, timeStatus.delayInMinutes)
                } else {
                    stringResource(R.string.arrived_on_time_time_status)
                }
            }
            is TimeStatus.OnTime -> stringResource(R.string.on_time)
            is TimeStatus.Unknown -> ""
        }
    }
}

@Composable
private fun PlatformText(platform: Platform, modifier: Modifier = Modifier) {
    val (textColour, fontWeight, platformName) = when (platform) {
        is Platform.Confirmed -> {
            if (platform.isChanged) {
                Triple(MaterialTheme.colorScheme.error, FontWeight.Bold, platform.platformName)
            } else {
                Triple(MaterialTheme.colorScheme.onBackground, FontWeight.Bold, platform.platformName)
            }
        }
        is Platform.Estimated -> {
            if (platform.isChanged) {
                Triple(MaterialTheme.colorScheme.error.copy(alpha = 0.3f), FontWeight.Normal, platform.platformName)
            } else {
                Triple(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f), FontWeight.Normal, platform.platformName)
            }
        }
    }
    Text(
        modifier = modifier,
        text = platformName,
        style = MaterialTheme.typography.bodyMedium.copy(color = textColour),
        fontWeight = fontWeight
    )
}