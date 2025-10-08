package me.cniekirk.ontrack.feature.home

import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import kotlinx.serialization.Serializable
import me.cniekirk.ontrack.core.di.viewmodel.metroViewModel

fun EntryProviderBuilder<NavKey>.home() {
    entry<Home> {
        val viewModel = metroViewModel<HomeViewModel>()
        HomeRoute(viewModel)
    }
}

@Serializable
data object Home : NavKey