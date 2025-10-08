package me.cniekirk.ontrack.core.data.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import me.cniekirk.ontrack.core.data.work.UpdateStationsWorker

@ContributesTo(AppScope::class)
interface DataProviders {

    val updateStationsWorkerFactory: UpdateStationsWorker.Factory
}