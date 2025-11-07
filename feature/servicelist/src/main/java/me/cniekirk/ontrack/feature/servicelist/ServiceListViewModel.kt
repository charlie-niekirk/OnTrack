package me.cniekirk.ontrack.feature.servicelist

import androidx.lifecycle.ViewModel
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import me.cniekirk.ontrack.core.di.viewmodel.ViewModelKey
import me.cniekirk.ontrack.core.domain.model.error.NetworkError
import me.cniekirk.ontrack.core.domain.model.services.TrainService
import me.cniekirk.ontrack.core.domain.repository.RealtimeTrainsRepository
import me.cniekirk.ontrack.core.domain.model.arguments.RequestTime
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceDetailRequest
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceListRequest
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceListType
import me.cniekirk.ontrack.core.domain.repository.RecentSearchesRepository
import me.cniekirk.ontrack.feature.servicelist.model.ServiceListError
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber

@AssistedInject
@ViewModelKey(ServiceListViewModel::class)
class ServiceListViewModel(
    @Assisted private val serviceListRequest: ServiceListRequest,
    private val realtimeTrainsRepository: RealtimeTrainsRepository,
    private val recentSearchesRepository: RecentSearchesRepository
) : ViewModel(), ContainerHost<ServiceListState, ServiceListEffect> {

    override val container = container<ServiceListState, ServiceListEffect>(
        ServiceListState(
            serviceListType = serviceListRequest.serviceListType,
            targetStation = serviceListRequest.targetStation.name,
            filterStation = serviceListRequest.filterStation?.name
        )
    ) {
        getTrainList()
        cacheRecentSearch()
    }

    fun getTrainList() = intent {
        fetchTrainServices()
            .onSuccess { trainServices ->
                reduce {
                    state.copy(
                        isLoading = false,
                        trainServiceList = trainServices
                    )
                }
            }
            .onFailure { error ->
                reduce {
                    state.copy(isLoading = false)
                }
                when (error) {
                    is NetworkError.HttpError, is NetworkError.Unknown, is NetworkError.SerializationError -> {
                        postSideEffect(ServiceListEffect.DisplayError(ServiceListError.FETCH_SERVICE_LIST_FAILED))
                    }
                    is NetworkError.NetworkFailure -> {
                        postSideEffect(ServiceListEffect.DisplayError(ServiceListError.NO_INTERNET))
                    }
                }
            }
    }

    private fun cacheRecentSearch() = intent {
        recentSearchesRepository.cacheRecentSearch(serviceListRequest)
            .onSuccess {
                Timber.v("Cached recent search successfully: $serviceListRequest")
            }
            .onFailure {
                Timber.e("Recent search cache failed: $serviceListRequest, error: $it")
            }
    }

    fun serviceSelected(serviceUid: String) = intent {
        val service = state.trainServiceList.first { it.serviceId == serviceUid }
        postSideEffect(
            ServiceListEffect.NavigateToServiceDetails(
                serviceDetailRequest = ServiceDetailRequest(
                    serviceUid = serviceUid,
                    year = service.runDate.year,
                    month = service.runDate.month,
                    day = service.runDate.day
                )
            )
        )
    }

    private suspend fun fetchTrainServices(): Result<List<TrainService>, NetworkError> {
        val filterStation = serviceListRequest.filterStation
        return when (val requestTime = serviceListRequest.requestTime) {
            is RequestTime.AtTime -> {
                when (serviceListRequest.serviceListType) {
                    ServiceListType.DEPARTURES -> {
                        if (filterStation != null) {
                            realtimeTrainsRepository.getDepartureBoardOnDateTimeTo(
                                fromStation = serviceListRequest.targetStation.crs,
                                toStation = filterStation.crs,
                                year = requestTime.year,
                                month = requestTime.month,
                                day = requestTime.day,
                                time = requestTime.hours + requestTime.mins
                            )
                        } else {
                            realtimeTrainsRepository.getDepartureBoardOnDateTime(
                                station = serviceListRequest.targetStation.crs,
                                year = requestTime.year,
                                month = requestTime.month,
                                day = requestTime.day,
                                time = requestTime.hours + requestTime.mins
                            )
                        }
                    }
                    ServiceListType.ARRIVALS -> {
                        if (filterStation != null) {
                            realtimeTrainsRepository.getArrivalBoardOnDateTimeFrom(
                                atStation = serviceListRequest.targetStation.crs,
                                fromStation = filterStation.crs,
                                year = requestTime.year,
                                month = requestTime.month,
                                day = requestTime.day,
                                time = requestTime.hours + requestTime.mins
                            )
                        } else {
                            realtimeTrainsRepository.getArrivalBoardOnDateTime(
                                station = serviceListRequest.targetStation.crs,
                                year = requestTime.year,
                                month = requestTime.month,
                                day = requestTime.day,
                                time = requestTime.hours + requestTime.mins
                            )
                        }
                    }
                }
            }
            is RequestTime.Now -> {
                when (serviceListRequest.serviceListType) {
                    ServiceListType.DEPARTURES -> {
                        if (filterStation != null) {
                            realtimeTrainsRepository.getCurrentDepartureBoardTo(
                                fromStation = serviceListRequest.targetStation.crs,
                                toStation = filterStation.crs
                            )
                        } else {
                            realtimeTrainsRepository.getCurrentDepartureBoard(
                                station = serviceListRequest.targetStation.crs
                            )
                        }
                    }
                    ServiceListType.ARRIVALS -> {
                        if (filterStation != null) {
                            realtimeTrainsRepository.getCurrentArrivalBoardFrom(
                                atStation = serviceListRequest.targetStation.crs,
                                fromStation = filterStation.crs
                            )
                        } else {
                            realtimeTrainsRepository.getCurrentArrivalBoard(
                                station = serviceListRequest.targetStation.crs
                            )
                        }
                    }
                }
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(serviceListRequest: ServiceListRequest): ServiceListViewModel
    }
}
