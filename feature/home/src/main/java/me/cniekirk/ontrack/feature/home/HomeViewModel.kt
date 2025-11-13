package me.cniekirk.ontrack.feature.home

import androidx.lifecycle.ViewModel
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import me.cniekirk.ontrack.core.di.viewmodel.ViewModelKey
import me.cniekirk.ontrack.core.di.viewmodel.ViewModelScope
import me.cniekirk.ontrack.core.domain.model.Station
import me.cniekirk.ontrack.core.domain.model.arguments.RequestTime
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceListRequest
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceListType
import me.cniekirk.ontrack.core.navigation.StationType
import me.cniekirk.ontrack.core.domain.model.arguments.TrainStation
import me.cniekirk.ontrack.core.domain.repository.RecentSearchesRepository
import me.cniekirk.ontrack.core.platform.TimeProvider
import me.cniekirk.ontrack.feature.home.state.HomeEffect
import me.cniekirk.ontrack.feature.home.state.HomeState
import me.cniekirk.ontrack.feature.home.state.QueryType
import me.cniekirk.ontrack.feature.home.state.StationSelection
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

private const val TIME_PAD_CHARACTER = '0'

@Inject
@ViewModelKey(HomeViewModel::class)
@ContributesIntoMap(ViewModelScope::class, binding = binding<ViewModel>())
class HomeViewModel(
    private val recentSearchesRepository: RecentSearchesRepository,
    private val timeProvider: TimeProvider
) : ViewModel(), ContainerHost<HomeState, HomeEffect> {

    override val container = container<HomeState, HomeEffect>(
        HomeState(currentDateMillis = timeProvider.currentDateMillis())
    ) {
        fetchRecentSearches()
    }

    private fun fetchRecentSearches() = intent {
        recentSearchesRepository.getRecentSearches()
            .onSuccess { recentSearches ->
                // Update state
                reduce {
                    state.copy(recentSearches = recentSearches)
                }
            }
            .onFailure {
                // Post error effect
                postSideEffect(HomeEffect.ShowFailedToFetchRecentSearchesError)
            }
    }

    fun updateQueryType(queryType: QueryType) = intent {
        reduce {
            state.copy(queryType = queryType)
        }
    }

    fun stationSelected(stationType: StationType, station: Station) = intent {
        when (stationType) {
            StationType.TARGET -> {
                reduce {
                    state.copy(targetStationSelection = StationSelection.Selected(station))
                }
            }
            StationType.FILTER -> {
                reduce {
                    state.copy(filterStationSelection = StationSelection.Selected(station))
                }
            }
        }
    }

    fun clearTargetStation() = intent {
        reduce {
            state.copy(targetStationSelection = StationSelection.None)
        }
    }

    fun clearFilterStation() = intent {
        reduce {
            state.copy(filterStationSelection = StationSelection.None)
        }
    }

    fun processSelectedDateTime(dateMillis: Long, hour: Int, minute: Int) = intent {
        val date = timeProvider.convertMillisToDate(dateMillis)
        val day = date.dayOfMonth
        val month = date.monthValue
        val year = date.year

        val hours = hour.toString().padStart(2, TIME_PAD_CHARACTER)
        val mins = minute.toString().padStart(2, TIME_PAD_CHARACTER)

        reduce {
            state.copy(
                requestTime = RequestTime.AtTime(
                    year = year.toString(),
                    month = month.toString(),
                    day = day.toString(),
                    hours = hours,
                    mins = mins
                )
            )
        }
    }

    fun resetDateTime() = intent {
        reduce {
            state.copy(
                requestTime = RequestTime.Now
            )
        }
    }

    fun searchTrains() = intent {
        when (val targetStation = state.targetStationSelection) {
            is StationSelection.None -> {
                // Invalid, post error
                postSideEffect(HomeEffect.ShowNoStationSelectedError)
            }
            is StationSelection.Selected -> {
                val serviceListRequest = when (val filterStation = state.filterStationSelection) {
                    is StationSelection.None -> {
                         ServiceListRequest(
                             serviceListType = if (state.queryType == QueryType.DEPARTURES) ServiceListType.DEPARTURES else ServiceListType.ARRIVALS,
                             requestTime = state.requestTime,
                             targetStation = TrainStation(
                                targetStation.station.crs,
                                targetStation.station.name
                            ),
                            filterStation = null
                        )
                    }
                    is StationSelection.Selected -> {
                        ServiceListRequest(
                            serviceListType = if (state.queryType == QueryType.DEPARTURES) ServiceListType.DEPARTURES else ServiceListType.ARRIVALS,
                            requestTime = state.requestTime,
                            targetStation = TrainStation(
                                targetStation.station.crs,
                                targetStation.station.name
                            ),
                            filterStation = TrainStation(
                                filterStation.station.crs,
                                filterStation.station.name
                            )
                        )
                    }
                }

                postSideEffect(HomeEffect.NavigateToServiceList(serviceListRequest))
            }
        }
    }

    fun clearAllRecentSearches() = intent {

    }
}