package me.cniekirk.ontrack.feature.servicedetail

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import me.cniekirk.ontrack.core.di.viewmodel.ViewModelKey
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@AssistedInject
@ViewModelKey(ServiceDetailViewModel::class)
class ServiceDetailViewModel(
    @Assisted private val serviceDetailRequest: ServiceDetailRequest
) : ViewModel(), ContainerHost<ServiceDetailState, ServiceDetailEffect> {

    override val container = container<ServiceDetailState, ServiceDetailEffect>(ServiceDetailState()) {

    }

    fun fetchServiceDetails(serviceDetailRequest: ServiceDetailRequest) = intent {

    }

    @AssistedFactory
    fun interface Factory {
        fun create(serviceDetailRequest: ServiceDetailRequest): ServiceDetailViewModel
    }
}
