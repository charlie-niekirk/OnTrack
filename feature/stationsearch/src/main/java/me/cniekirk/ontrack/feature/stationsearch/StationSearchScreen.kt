package me.cniekirk.ontrack.feature.stationsearch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.cniekirk.ontrack.core.compose.theme.OnTrackTheme

@Composable
internal fun StationSearchRoute(viewModel: StationSearchViewModel) {

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StationSearchScreen(state: StationSearchState) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.isLoading) {
            Spacer(modifier = Modifier.weight(1f))
            CircularWavyProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.weight(1f))
        } else {

        }
    }
}

@Preview
@Composable
private fun StationSearchScreenPreview() {
    val state = StationSearchState()

    OnTrackTheme {
        Surface {
            StationSearchScreen(state = state)
        }
    }
}