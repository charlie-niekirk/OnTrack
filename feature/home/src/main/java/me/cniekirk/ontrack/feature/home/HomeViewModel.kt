package me.cniekirk.ontrack.feature.home

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import me.cniekirk.ontrack.core.di.viewmodel.ViewModelKey
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@Inject
@ViewModelKey(HomeViewModel::class)
@ContributesIntoMap(HomeViewModel::class, binding = binding<ViewModel>())
class HomeViewModel : ViewModel(), ContainerHost<HomeState, HomeEffect> {

    override val container = container<HomeState, HomeEffect>(HomeState()) {

    }
}