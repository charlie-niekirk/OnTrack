package me.cniekirk.ontrack.feature.stationsearch.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.cniekirk.ontrack.core.domain.model.Station
import me.cniekirk.ontrack.core.compose.theme.OnTrackTheme

@Composable
fun StationList(
    stations: List<Station>,
    onStationClicked: (Station) -> Unit
) {
    LazyColumn {
        items(stations) { station ->
            StationListItem(
                station = station,
                onStationClicked = onStationClicked
            )
        }
    }
}

@Composable
fun StationListItem(
    station: Station,
    onStationClicked: (Station) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStationClicked(station) }
            .padding(16.dp)
    ) {
        Text(
            text = station.name,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            modifier = Modifier.alpha(0.8f),
            text = station.crs,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview
@Composable
private fun StationListPreview() {
    val stations = listOf(
        Station("London Paddington", "PAD"),
        Station("London Euston", "EUS"),
        Station("London Bridge", "LBG"),
    )
    OnTrackTheme {
        Surface {
            StationList(stations = stations, onStationClicked = {})
        }
    }
}