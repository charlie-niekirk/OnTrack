package me.cniekirk.ontrack.di

import android.app.Activity
import android.app.Application
import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides
import me.cniekirk.ontrack.core.data.di.DataProviders
import me.cniekirk.ontrack.core.database.di.DatabaseProviders
import me.cniekirk.ontrack.core.di.components.ApplicationContext
import me.cniekirk.ontrack.core.di.viewmodel.ViewModelGraph
import me.cniekirk.ontrack.core.network.di.NetworkProviders
import kotlin.reflect.KClass

@DependencyGraph(AppScope::class)
interface OnTrackGraph : ViewModelGraph.Factory, DataProviders, NetworkProviders, DatabaseProviders {

    @Provides
    @ApplicationContext
    fun provideApplicationContext(application: Application): Context = application

    @Multibinds
    val activityProviders: Map<KClass<out Activity>, Provider<Activity>>

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): OnTrackGraph
    }
}