package me.cniekirk.ontrack.feature.home.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.cniekirk.ontrack.core.compose.theme.OnTrackTheme
import me.cniekirk.ontrack.core.domain.model.Station
import me.cniekirk.ontrack.feature.home.R
import me.cniekirk.ontrack.feature.home.state.StationSelection

@Composable
internal fun StationCard(
    stationSelection: StationSelection,
    @StringRes placeholder: Int,
    onClick: () -> Unit,
    onClearSelectionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val queryStationText = when (stationSelection) {
            is StationSelection.None -> stringResource(id = placeholder)
            is StationSelection.Selected -> stationSelection.station.name
        }
        Text(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 12.dp,
                bottom = 12.dp
            ),
            text = queryStationText,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(modifier = Modifier.padding(end = 8.dp)) {
            when (stationSelection) {
                is StationSelection.None -> {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
                is StationSelection.Selected -> {
                    IconButton(onClick = onClearSelectionClick) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.cd_clear_station)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun StationCardSelectedPreview() {
    OnTrackTheme {
        Surface {
            StationCard(
                stationSelection = StationSelection.Selected(Station("London Bridge", "LBG", "")),
                placeholder = R.string.empty_departing_station,
                onClick = {},
                onClearSelectionClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun StationCardNonePreview() {
    OnTrackTheme {
        Surface {
            StationCard(
                stationSelection = StationSelection.None,
                placeholder = R.string.empty_departing_station,
                onClick = {},
                onClearSelectionClick = {}
            )
        }
    }
}