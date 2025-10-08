package me.cniekirk.ontrack.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import me.cniekirk.ontrack.core.di.viewmodel.ViewModelGraph
import timber.log.Timber

//@ContributesBinding(AppScope::class)
//@Inject
//class MetroViewModelFactory(val appGraph: OnTrackGraph) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
//        val viewModelGraph = viewModelGraph(extras)
//
//        Timber.Forest.d("Providers: ${viewModelGraph.viewModelProviders}")
//
//        val provider =
//            viewModelGraph.viewModelProviders[modelClass.kotlin]
//                ?: throw IllegalArgumentException("Unknown model class $modelClass")
//
//        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
//        return modelClass.cast(provider())
//    }
//
//    fun viewModelGraph(extras: CreationExtras): ViewModelGraph = appGraph.createViewModelGraph(extras)
//}