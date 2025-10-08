package me.cniekirk.ontrack.feature.stationsearch

import androidx.lifecycle.ViewModel
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import me.cniekirk.ontrack.core.di.viewmodel.ViewModelKey
import me.cniekirk.ontrack.core.domain.repository.StationsRepository
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber

@Inject
@ViewModelKey(StationSearchViewModel::class)
@ContributesIntoMap(StationSearchViewModel::class, binding = binding<ViewModel>())
internal class StationSearchViewModel(
    private val stationsRepository: StationsRepository
) : ViewModel(), ContainerHost<StationSearchState, StationSearchEffect> {

    override val container = container<StationSearchState, StationSearchEffect>(StationSearchState()) {
        getAllStations()
    }

    private fun getAllStations() = intent {
        stationsRepository.getStations()
            .onSuccess { response ->
                reduce { state.copy(stations = response) }
            }
            .onFailure { error ->
                Timber.e("Error: $error")
                postSideEffect(StationSearchEffect.ShowError(R.string.station_fetch_error))
            }
    }
}