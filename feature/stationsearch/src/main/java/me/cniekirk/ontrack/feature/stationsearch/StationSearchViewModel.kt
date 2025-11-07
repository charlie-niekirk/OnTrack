package me.cniekirk.ontrack.feature.stationsearch

import androidx.lifecycle.ViewModel
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import me.cniekirk.ontrack.core.di.viewmodel.ViewModelKey
import me.cniekirk.ontrack.core.di.viewmodel.ViewModelScope
import me.cniekirk.ontrack.core.domain.model.Station
import me.cniekirk.ontrack.core.domain.repository.StationsRepository
import me.cniekirk.ontrack.core.navigation.StationType
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber

@AssistedInject
@ViewModelKey(StationSearchViewModel::class)
class StationSearchViewModel(
    @Assisted private val stationType: StationType,
    private val stationsRepository: StationsRepository
) : ViewModel(), ContainerHost<StationSearchState, StationSearchEffect> {

    // Simple in memory cache to make querying stations fast
    private var stationList: List<Station> = emptyList()

    override val container = container<StationSearchState, StationSearchEffect>(StationSearchState(stationType)) {
        getAllStations()
    }

    fun searchStations(query: String) = intent {
        reduce { state.copy(searchQuery = query) }
        val filtered = stationList.filter {
            it.crs.contains(query, ignoreCase = true) || it.name.contains(query, ignoreCase = true)
        }
        reduce { state.copy(stations = filtered) }
    }

    private fun getAllStations() = intent {
        stationsRepository.getStations()
            .onSuccess { response ->
                stationList = response
                reduce {
                    state.copy(
                        isLoading = false,
                        stations = response
                    )
                }
            }
            .onFailure { error ->
                Timber.e("Error: $error")
                reduce { state.copy(isLoading = false) }
                postSideEffect(StationSearchEffect.ShowError(R.string.station_fetch_error))
            }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(stationType: StationType): StationSearchViewModel
    }
}