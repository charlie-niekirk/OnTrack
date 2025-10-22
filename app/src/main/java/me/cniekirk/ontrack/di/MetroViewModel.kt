package me.cniekirk.ontrack.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.reflect.KClass

// Define the CompositionLocal for the ViewModelGraph creator function
// This decouples the composable from app-specific graphs/factories
val LocalViewModelGraphFactory = compositionLocalOf<(CreationExtras) -> ViewModelGraph> {
    error("No ViewModelGraph factory provided via LocalViewModelGraphFactory")
}

@Composable
inline fun <reified VM : ViewModel> metroViewModel(
    viewModelStoreOwner: ViewModelStoreOwner =
        checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
    key: String? = null,
): VM {
    val graphFactory = LocalViewModelGraphFactory.current
    val factory = remember {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val viewModelGraph = graphFactory(extras)
                val modelKClass: KClass<out ViewModel> = modelClass.kotlin
                val provider = viewModelGraph.viewModelProviders[modelKClass]
                    ?: throw IllegalArgumentException("Unknown model class $modelClass")
                @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNCHECKED_CAST")
                return provider() as T
            }
        }
    }
    return viewModel(
        modelClass = VM::class.java,
        viewModelStoreOwner = viewModelStoreOwner,
        key = key,
        factory = factory
    )
}

@Composable
inline fun <reified VM : ViewModel> metroViewModel(
    viewModelStoreOwner: ViewModelStoreOwner =
        checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
    key: String? = null,
    crossinline factory: ViewModelGraph.() -> VM,
): VM {
    val graphFactory = LocalViewModelGraphFactory.current
    val vmFactory = remember {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val viewModelGraph = graphFactory(extras)
                @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNCHECKED_CAST")
                return viewModelGraph.factory() as T
            }
        }
    }
    return viewModel(
        modelClass = VM::class.java,
        viewModelStoreOwner = viewModelStoreOwner,
        key = key,
        factory = vmFactory
    )
}