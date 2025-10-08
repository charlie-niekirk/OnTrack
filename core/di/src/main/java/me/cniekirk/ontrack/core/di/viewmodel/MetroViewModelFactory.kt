package me.cniekirk.ontrack.core.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import timber.log.Timber

@ContributesBinding(AppScope::class)
@Inject
class MetroViewModelFactory(val viewModelGraph: ViewModelGraph) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val viewModelGraph = viewModelGraph(extras)

        Timber.Forest.d("Providers: ${viewModelGraph.viewModelProviders}")

        val provider =
            viewModelGraph.viewModelProviders[modelClass.kotlin]
                ?: throw IllegalArgumentException("Unknown model class $modelClass")

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        return modelClass.cast(provider())
    }

    fun viewModelGraph(extras: CreationExtras): ViewModelGraph = viewModelGraph.createViewModelGraph(extras)
}