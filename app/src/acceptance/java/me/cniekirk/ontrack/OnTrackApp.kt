package me.cniekirk.ontrack

import android.app.Application
import dev.zacsweers.metro.createDynamicGraphFactory
import me.cniekirk.ontrack.di.OnTrackGraph
import me.cniekirk.ontrack.testing.di.TestBindingContainer
import timber.log.Timber

/**
 * Test version of OnTrackApp that uses Metro's dynamic dependency graphs
 * to inject fake repositories for baseline profile testing.
 *
 * This class is only available in benchmark builds and uses Metro's `createDynamicGraph`
 * function to override the real repository bindings with fake implementations.
 */
class OnTrackApp : Application() {

    val appGraph by lazy {
        Timber.d("Creating app graph with test dependencies for baseline profile testing")
        createDynamicGraphFactory<OnTrackGraph.Factory>(TestBindingContainer).create(this)
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
