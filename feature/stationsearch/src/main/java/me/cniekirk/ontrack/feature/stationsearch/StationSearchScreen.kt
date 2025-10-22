package me.cniekirk.ontrack.feature.stationsearch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.cniekirk.ontrack.core.compose.theme.OnTrackTheme
import me.cniekirk.ontrack.core.domain.model.Station
import me.cniekirk.ontrack.core.navigation.StationResult
import me.cniekirk.ontrack.core.navigation.StationType
import me.cniekirk.ontrack.feature.stationsearch.components.StationList
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun StationSearchRoute(
    viewModel: StationSearchViewModel,
    stationSelected: (StationResult) -> Unit
) {
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is StationSearchEffect.ShowError -> {

            }
        }
    }

    StationSearchScreen(
        state = state,
        onQueryChanged = viewModel::searchStations,
        onStationClicked = {
            stationSelected(
                StationResult(
                    stationType = state.stationType,
                    crs = it.crs,
                    name = it.name
                )
            )
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StationSearchScreen(
    state: StationSearchState,
    onQueryChanged: (String) -> Unit,
    onStationClicked: (Station) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        if (state.isLoading) {
            Spacer(modifier = Modifier.weight(1f))
            CircularWavyProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.weight(1f))
        } else {
            var searchText by remember { mutableStateOf("") }

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp),
                value = searchText,
                onValueChange = {
                    searchText = it
                    onQueryChanged(it)
                },
                placeholder = {
                    Text(text = stringResource(R.string.station_search_placeholder))
                }
            )

            StationList(
                modifier = Modifier.padding(top = 16.dp),
                stations = state.stations,
                onStationClicked = onStationClicked
            )
        }
    }
}

@Preview
@Composable
private fun StationSearchScreenPreview() {
    val state = StationSearchState(
        stationType = StationType.TARGET,
        isLoading = false,
        stations = listOf(
            Station("London Paddington", "PAD"),
            Station("London Euston", "EUS"),
            Station("London Bridge", "LBG"),
        )
    )

    OnTrackTheme {
        Surface {
            StationSearchScreen(state = state, onQueryChanged = {}, onStationClicked = {})
        }
    }
}