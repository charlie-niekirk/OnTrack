package me.cniekirk.ontrack.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.cniekirk.ontrack.core.compose.theme.OnTrackTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun HomeRoute(viewModel: HomeViewModel) {
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            else -> {}
        }
    }

    HomeScreen(state = state)
}

@Composable
private fun HomeScreen(state: HomeState) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    val state = HomeState()

    OnTrackTheme {
        Surface {
            HomeScreen(state = state)
        }
    }
}
