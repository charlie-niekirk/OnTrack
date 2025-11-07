package me.cniekirk.ontrack.feature.servicedetail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.cniekirk.ontrack.core.compose.theme.OnTrackTheme
import me.cniekirk.ontrack.core.domain.model.servicedetails.Location
import me.cniekirk.ontrack.core.domain.model.servicedetails.ServiceDetails
import me.cniekirk.ontrack.core.domain.model.services.Platform
import me.cniekirk.ontrack.core.domain.model.services.ServiceLocation
import me.cniekirk.ontrack.core.domain.model.services.TimeStatus
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ServiceDetailRoute(viewModel: ServiceDetailViewModel) {
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ServiceDetailEffect.ShowError -> {
                // TODO: Handle error
            }
        }
    }

    ServiceDetailScreen(state = state)
}

@Composable
private fun ServiceDetailScreen(state: ServiceDetailState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.serviceDetails != null -> {
                ServiceDetailContent(serviceDetails = state.serviceDetails)
            }
        }
    }
}

@Composable
private fun ServiceDetailContent(serviceDetails: ServiceDetails) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        // Route header
        RouteHeader(
            origin = serviceDetails.origin,
            destination = serviceDetails.destination,
            trainOperatingCompany = serviceDetails.trainOperatingCompany
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Location timeline
        LocationTimeline(
            locations = serviceDetails.locations
        )
    }
}

@Composable
private fun RouteHeader(
    origin: String,
    destination: String,
    trainOperatingCompany: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.route_format, origin, destination),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = trainOperatingCompany,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun LocationTimeline(locations: List<Location>) {
    val currentLocationIndex = getCurrentLocationIndex(locations)

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(locations) { index, location ->
            LocationItem(
                location = location,
                isCurrentLocation = index == currentLocationIndex,
                isFirst = index == 0,
                isLast = index == locations.lastIndex,
                isPrevious = index < currentLocationIndex
            )
        }
    }
}

@Composable
private fun LocationItem(
    location: Location,
    isCurrentLocation: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
    isPrevious: Boolean
) {
    val alpha = if (isPrevious) 0.3f else 1f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Max)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Timeline indicator
        TimelineIndicator(
            isCurrentLocation = isCurrentLocation,
            isFirst = isFirst,
            isLast = isLast,
            isPrevious = isPrevious
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Location details
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            // Station name
            Text(
                text = location.locationName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isCurrentLocation) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrentLocation) {
                    MaterialTheme.colorScheme.primary.copy(alpha = alpha)
                } else {
                    MaterialTheme.colorScheme.onBackground.copy(alpha = alpha)
                }
            )

            Spacer(Modifier.height(2.dp))

            Column {
                // Arrival time
                if (location.arrivalTimeStatus !is TimeStatus.Unknown) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.arrives),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f * alpha)
                        )
                        TimeStatusText(
                            timeStatus = location.arrivalTimeStatus,
                            isDeparture = false,
                            isPrevious = isPrevious
                        )
                    }
                }

                Spacer(Modifier.height(2.dp))

                // Departure time
                if (location.departureTimeStatus !is TimeStatus.Unknown) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.departs),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f * alpha)
                        )
                        TimeStatusText(
                            timeStatus = location.departureTimeStatus,
                            isDeparture = true,
                            isPrevious = isPrevious
                        )
                    }
                }
            }

            Spacer(Modifier.height(2.dp))

            // Platform
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.platform),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f * alpha)
                )
                PlatformText(
                    platform = location.platform,
                    isPrevious = isPrevious
                )
            }

            // Service location status
            location.serviceLocation?.let { serviceLocation ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = getServiceLocationText(serviceLocation),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun TimelineIndicator(
    isCurrentLocation: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
    isPrevious: Boolean
) {
    val alpha = if (isPrevious) 0.3f else 1f
    val lineColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f * alpha)
    val circleColor = if (isCurrentLocation) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f * alpha)
    }
    val circleRadius = if (isCurrentLocation) 8.dp else 6.dp

    Canvas(
        modifier = Modifier
            .width(24.dp)
            .fillMaxHeight()
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerX = canvasWidth / 2f
        val centerY = canvasHeight / 2f
        val lineWidth = 2.dp.toPx()
        val radius = circleRadius.toPx()

        // Draw line from top to center (if not first item)
        if (!isFirst) {
            val topAlpha = if (isPrevious or isCurrentLocation) 0.3f else 1f
            drawLine(
                color = lineColor.copy(alpha = topAlpha),
                start = Offset(centerX, 0f),
                end = Offset(centerX, centerY - radius),
                strokeWidth = lineWidth
            )
        }

        // Draw line from center to bottom (if not last item)
        if (!isLast) {
            drawLine(
                color = lineColor.copy(alpha = alpha),
                start = Offset(centerX, centerY + radius),
                end = Offset(centerX, canvasHeight),
                strokeWidth = lineWidth
            )
        }

        // Draw circle indicator
        drawCircle(
            color = circleColor,
            radius = radius,
            center = Offset(centerX, centerY)
        )
    }
}

@Composable
private fun TimeStatusText(
    timeStatus: TimeStatus,
    isPrevious: Boolean,
    isDeparture: Boolean
) {
    val alphaMultiplier = if (isPrevious) 0.3f else 1f
    val (text, color) = when (timeStatus) {
        is TimeStatus.Departed -> {
            val timeText = stringResource(R.string.departed_time, timeStatus.actualDepartureTime)
            val finalText = if (timeStatus.delayInMinutes > 0) {
                stringResource(R.string.time_with_delay, timeText, timeStatus.delayInMinutes)
            } else {
                timeText
            }
            finalText to if (timeStatus.delayInMinutes > 0) {
                MaterialTheme.colorScheme.error.copy(alpha = 0.8f * alphaMultiplier)
            } else {
                MaterialTheme.colorScheme.onBackground.copy(alpha = alphaMultiplier)
            }
        }
        is TimeStatus.Arrived -> {
            val timeText = stringResource(R.string.arrived_time, timeStatus.actualArrivalTime)
            val finalText = if (timeStatus.delayInMinutes > 0) {
                stringResource(R.string.time_with_delay, timeText, timeStatus.delayInMinutes)
            } else {
                timeText
            }
            finalText to if (timeStatus.delayInMinutes > 0) {
                MaterialTheme.colorScheme.error.copy(alpha = 0.8f * alphaMultiplier)
            } else {
                MaterialTheme.colorScheme.onBackground.copy(alpha = alphaMultiplier)
            }
        }
        is TimeStatus.OnTime -> {
            timeStatus.scheduledTime to MaterialTheme.colorScheme.onBackground.copy(alpha = alphaMultiplier)
        }
        is TimeStatus.Delayed -> {
            val text = stringResource(
                R.string.delayed_time,
                timeStatus.estimatedTime,
                timeStatus.delayInMinutes
            )
            text to MaterialTheme.colorScheme.error.copy(alpha = 0.8f * alphaMultiplier)
        }
        is TimeStatus.Cancelled -> {
            stringResource(R.string.cancelled) to MaterialTheme.colorScheme.error.copy(alpha = alphaMultiplier)
        }
        is TimeStatus.Unknown -> {
            "" to MaterialTheme.colorScheme.onBackground.copy(alpha = alphaMultiplier)
        }
    }

    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = color,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun PlatformText(platform: Platform, isPrevious: Boolean) {
    val (textColor, fontWeight, platformName) = when (platform) {
        is Platform.Confirmed -> {
            if (platform.isChanged) {
                Triple(
                    MaterialTheme.colorScheme.error,
                    FontWeight.Bold,
                    platform.platformName
                )
            } else {
                Triple(
                    MaterialTheme.colorScheme.onBackground,
                    FontWeight.Bold,
                    platform.platformName
                )
            }
        }
        is Platform.Estimated -> {
            if (platform.isChanged) {
                Triple(
                    MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                    FontWeight.Normal,
                    platform.platformName
                )
            } else {
                Triple(
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    FontWeight.Normal,
                    platform.platformName
                )
            }
        }
    }

    val alpha = if (isPrevious) 0.3f else 1f

    Text(
        text = platformName,
        style = MaterialTheme.typography.bodySmall.copy(color = textColor.copy(alpha = alpha)),
        fontWeight = fontWeight
    )
}

// Helper functions

private fun getCurrentLocationIndex(locations: List<Location>): Int {
    // Find the location where the train currently is based on time status

    // Priority 1: Check for service location status (most accurate)
    locations.forEachIndexed { index, location ->
        location.serviceLocation?.let {
            when (it) {
                ServiceLocation.APPROACHING_STATION,
                ServiceLocation.APPROACHING_PLATFORM,
                ServiceLocation.AT_PLATFORM,
                ServiceLocation.PREPARING_DEPARTURE,
                ServiceLocation.READY_TO_DEPART -> return index
            }
        }
    }

    // Priority 2: Find last departed or arrived location
    val lastDepartedOrArrivedIndex = locations.indexOfLast { location ->
        location.departureTimeStatus is TimeStatus.Departed ||
        location.arrivalTimeStatus is TimeStatus.Arrived
    }

    if (lastDepartedOrArrivedIndex != -1) {
        // If we found a departed/arrived location and it's not the last one,
        // the train is likely at or approaching the next location
        return if (lastDepartedOrArrivedIndex < locations.lastIndex) {
            lastDepartedOrArrivedIndex + 1
        } else {
            lastDepartedOrArrivedIndex
        }
    }

    // Priority 3: Find first delayed or on-time location (upcoming)
    val nextLocationIndex = locations.indexOfFirst { location ->
        location.departureTimeStatus is TimeStatus.Delayed ||
        location.departureTimeStatus is TimeStatus.OnTime
    }

    return if (nextLocationIndex != -1) nextLocationIndex else 0
}

@Composable
private fun getServiceLocationText(serviceLocation: ServiceLocation): String {
    return when (serviceLocation) {
        ServiceLocation.APPROACHING_STATION -> stringResource(R.string.service_location_approaching_station)
        ServiceLocation.APPROACHING_PLATFORM -> stringResource(R.string.service_location_approaching_platform)
        ServiceLocation.AT_PLATFORM -> stringResource(R.string.service_location_at_platform)
        ServiceLocation.PREPARING_DEPARTURE -> stringResource(R.string.service_location_preparing_departure)
        ServiceLocation.READY_TO_DEPART -> stringResource(R.string.service_location_ready_to_depart)
    }
}

// Previews

@Preview(showBackground = true)
@Composable
private fun PreviewLoadingState() {
    OnTrackTheme {
        ServiceDetailScreen(
            state = ServiceDetailState(
                isLoading = true,
                serviceDetails = null
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewServiceDetailsScreen() {
    OnTrackTheme {
        ServiceDetailScreen(
            state = ServiceDetailState(
                isLoading = false,
                serviceDetails = createPreviewServiceDetails()
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRouteHeader() {
    OnTrackTheme {
        RouteHeader(
            origin = "Leeds",
            destination = "London Kings Cross",
            trainOperatingCompany = "LNER"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLocationItemCurrent() {
    OnTrackTheme {
        LocationItem(
            location = Location(
                locationName = "York",
                departureTimeStatus = TimeStatus.OnTime("14:30"),
                arrivalTimeStatus = TimeStatus.Arrived("14:25", "14:25", 0),
                platform = Platform.Confirmed("3", false),
                serviceLocation = ServiceLocation.AT_PLATFORM
            ),
            isCurrentLocation = true,
            isFirst = false,
            isLast = false,
            isPrevious = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLocationItemDeparted() {
    OnTrackTheme {
        LocationItem(
            location = Location(
                locationName = "Leeds",
                departureTimeStatus = TimeStatus.Departed("14:00", "14:00", 0),
                arrivalTimeStatus = TimeStatus.Unknown,
                platform = Platform.Confirmed("5", false),
                serviceLocation = null
            ),
            isCurrentLocation = false,
            isFirst = true,
            isLast = false,
            isPrevious = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLocationItemUpcoming() {
    OnTrackTheme {
        LocationItem(
            location = Location(
                locationName = "London Kings Cross",
                departureTimeStatus = TimeStatus.Unknown,
                arrivalTimeStatus = TimeStatus.OnTime("15:45"),
                platform = Platform.Estimated("7", false),
                serviceLocation = null
            ),
            isCurrentLocation = false,
            isFirst = false,
            isLast = true,
            isPrevious = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLocationItemDelayed() {
    OnTrackTheme {
        LocationItem(
            location = Location(
                locationName = "Doncaster",
                departureTimeStatus = TimeStatus.Delayed("14:50", "14:55", 5),
                arrivalTimeStatus = TimeStatus.Delayed("14:45", "14:50", 5),
                platform = Platform.Confirmed("2", true),
                serviceLocation = null
            ),
            isCurrentLocation = false,
            isFirst = false,
            isLast = false,
            isPrevious = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewPlatformTextConfirmed() {
    OnTrackTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            PlatformText(platform = Platform.Confirmed("5", false), isPrevious = false)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewPlatformTextEstimated() {
    OnTrackTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            PlatformText(platform = Platform.Estimated("3", false), isPrevious = false)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewPlatformTextChanged() {
    OnTrackTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            PlatformText(platform = Platform.Confirmed("7", true), isPrevious = false)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTimeStatusOnTime() {
    OnTrackTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TimeStatusText(
                timeStatus = TimeStatus.OnTime("14:30"),
                isDeparture = true,
                isPrevious = false
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTimeStatusDelayed() {
    OnTrackTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TimeStatusText(
                timeStatus = TimeStatus.Delayed("14:30", "14:35", 5),
                isDeparture = true,
                isPrevious = false
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTimeStatusDeparted() {
    OnTrackTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TimeStatusText(
                timeStatus = TimeStatus.Departed("14:30", "14:30", 0),
                isDeparture = true,
                isPrevious = false
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTimeStatusCancelled() {
    OnTrackTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TimeStatusText(
                timeStatus = TimeStatus.Cancelled("14:30", "Engineering works"),
                isDeparture = true,
                isPrevious = false
            )
        }
    }
}

// Helper for preview data
private fun createPreviewServiceDetails(): ServiceDetails {
    return ServiceDetails(
        trainOperatingCompany = "LNER",
        origin = "Leeds",
        destination = "London Kings Cross",
        locations = listOf(
            Location(
                locationName = "Leeds",
                departureTimeStatus = TimeStatus.Departed("14:00", "14:00", 0),
                arrivalTimeStatus = TimeStatus.Unknown,
                platform = Platform.Confirmed("5", false),
                serviceLocation = null
            ),
            Location(
                locationName = "Wakefield Westgate",
                departureTimeStatus = TimeStatus.Departed("14:15", "14:15", 0),
                arrivalTimeStatus = TimeStatus.Arrived("14:13", "14:13", 0),
                platform = Platform.Confirmed("2", false),
                serviceLocation = null
            ),
            Location(
                locationName = "Doncaster",
                departureTimeStatus = TimeStatus.OnTime("14:35"),
                arrivalTimeStatus = TimeStatus.Arrived("14:30", "14:30", 0),
                platform = Platform.Confirmed("3", false),
                serviceLocation = ServiceLocation.AT_PLATFORM
            ),
            Location(
                locationName = "Retford",
                departureTimeStatus = TimeStatus.OnTime("14:52"),
                arrivalTimeStatus = TimeStatus.OnTime("14:50"),
                platform = Platform.Estimated("1", false),
                serviceLocation = null
            ),
            Location(
                locationName = "Newark North Gate",
                departureTimeStatus = TimeStatus.OnTime("15:05"),
                arrivalTimeStatus = TimeStatus.OnTime("15:03"),
                platform = Platform.Estimated("2", false),
                serviceLocation = null
            ),
            Location(
                locationName = "Peterborough",
                departureTimeStatus = TimeStatus.OnTime("15:28"),
                arrivalTimeStatus = TimeStatus.OnTime("15:25"),
                platform = Platform.Estimated("3", false),
                serviceLocation = null
            ),
            Location(
                locationName = "London Kings Cross",
                departureTimeStatus = TimeStatus.Unknown,
                arrivalTimeStatus = TimeStatus.OnTime("16:15"),
                platform = Platform.Estimated("7", false),
                serviceLocation = null
            )
        )
    )
}
