package me.cniekirk.ontrack.navigation

import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import me.cniekirk.ontrack.core.compose.navigation.LocalResultEventBus
import me.cniekirk.ontrack.core.compose.navigation.ResultEffect
import me.cniekirk.ontrack.core.compose.theme.enterAnimation
import me.cniekirk.ontrack.core.compose.theme.exitAnimation
import me.cniekirk.ontrack.core.compose.theme.popEnterAnimation
import me.cniekirk.ontrack.core.compose.theme.popExitAnimation
import me.cniekirk.ontrack.core.domain.model.Station
import me.cniekirk.ontrack.core.navigation.ResultEventBus
import me.cniekirk.ontrack.core.navigation.StationResult
import me.cniekirk.ontrack.di.metroViewModel
import me.cniekirk.ontrack.feature.home.Home
import me.cniekirk.ontrack.feature.home.HomeRoute
import me.cniekirk.ontrack.feature.home.HomeViewModel
import me.cniekirk.ontrack.feature.servicelist.ServiceList
import me.cniekirk.ontrack.feature.servicelist.ServiceListRoute
import me.cniekirk.ontrack.feature.servicelist.ServiceListViewModel
import me.cniekirk.ontrack.feature.stationsearch.StationSearch
import me.cniekirk.ontrack.feature.stationsearch.StationSearchRoute
import me.cniekirk.ontrack.feature.stationsearch.StationSearchViewModel

@Composable
fun OnTrackNavigation(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(Home)

    val resultBus = remember { ResultEventBus() }

    CompositionLocalProvider(LocalResultEventBus.provides(resultBus)) {
        NavDisplay(
            modifier = modifier,
            entryDecorators = listOf(
                // Add the default decorators for managing scenes and saving state
                rememberSceneSetupNavEntryDecorator(),
                rememberSavedStateNavEntryDecorator(),
                // Then add the view model store decorator
                rememberViewModelStoreNavEntryDecorator()
            ),
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            transitionSpec = { enterAnimation() togetherWith exitAnimation() },
            popTransitionSpec = { popEnterAnimation() togetherWith popExitAnimation() },
            predictivePopTransitionSpec = { popEnterAnimation() togetherWith popExitAnimation() },
            entryProvider = entryProvider {
                entry<Home> {
                    val viewModel = metroViewModel<HomeViewModel>()

                    ResultEffect<StationResult> { stationResult ->
                        viewModel.stationSelected(
                            stationType = stationResult.stationType,
                            Station(stationResult.name, stationResult.crs)
                        )
                    }

                    HomeRoute(
                        viewModel = viewModel,
                        navigateToStationSelection = { stationType ->
                            backStack.add(StationSearch(stationType))
                        },
                        navigateToServiceList = { serviceListRequest ->
                            backStack.add(ServiceList(serviceListRequest))
                        }
                    )
                }
                entry<StationSearch> { stationSearch ->
                    val viewModel = metroViewModel<StationSearchViewModel> { stationSearchFactory.create(stationSearch.stationType) }
                    StationSearchRoute(viewModel) { stationResult ->
                        resultBus.sendResult<StationResult>(result = stationResult)
                        backStack.removeLastOrNull()
                    }
                }
                entry<ServiceList> { serviceList ->
                    val viewModel = metroViewModel<ServiceListViewModel> { serviceListFactory.create(serviceList.serviceListRequest) }
                    ServiceListRoute(viewModel)
                }
            }
        )
    }
}