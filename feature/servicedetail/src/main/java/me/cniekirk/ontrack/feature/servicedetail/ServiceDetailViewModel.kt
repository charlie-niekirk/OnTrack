package me.cniekirk.ontrack.feature.servicedetail

import androidx.lifecycle.ViewModel
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import me.cniekirk.ontrack.core.di.viewmodel.ViewModelKey
import me.cniekirk.ontrack.core.domain.model.error.NetworkError
import me.cniekirk.ontrack.core.domain.repository.RealtimeTrainsRepository
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceDetailRequest
import me.cniekirk.ontrack.feature.servicedetail.model.ServiceDetailError
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@AssistedInject
@ViewModelKey(ServiceDetailViewModel::class)
class ServiceDetailViewModel(
    @Assisted private val serviceDetailRequest: ServiceDetailRequest,
    private val realtimeTrainsRepository: RealtimeTrainsRepository
) : ViewModel(), ContainerHost<ServiceDetailState, ServiceDetailEffect> {

    override val container = container<ServiceDetailState, ServiceDetailEffect>(ServiceDetailState()) {
        fetchServiceDetails()
    }

    fun fetchServiceDetails() = intent {
        realtimeTrainsRepository.getServiceDetails(
            serviceUid = serviceDetailRequest.serviceUid,
            year = serviceDetailRequest.year,
            month = serviceDetailRequest.month,
            day = serviceDetailRequest.day
        ).onSuccess { response ->
            reduce {
                state.copy(
                    isLoading = false,
                    serviceDetails = response
                )
            }
        }.onFailure { responseError ->
            reduce {
                state.copy(isLoading = false)
            }
            val error = when (responseError) {
                is NetworkError.HttpError, is NetworkError.SerializationError -> ServiceDetailError.SERVER_ERROR
                is NetworkError.NetworkFailure, is NetworkError.Unknown -> ServiceDetailError.NETWORK_ERROR
            }
            postSideEffect(ServiceDetailEffect.ShowError(error))
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(serviceDetailRequest: ServiceDetailRequest): ServiceDetailViewModel
    }
}
