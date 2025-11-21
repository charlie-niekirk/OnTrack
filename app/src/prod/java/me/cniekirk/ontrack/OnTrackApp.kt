package me.cniekirk.ontrack

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dev.zacsweers.metro.createGraphFactory
import me.cniekirk.ontrack.core.data.work.UpdateStationsWorker
import me.cniekirk.ontrack.di.DelegatingWorkerFactory
import me.cniekirk.ontrack.di.OnTrackGraph
import timber.log.Timber
import java.util.concurrent.TimeUnit

class OnTrackApp : Application() {

    open val appGraph by lazy {
        createGraphFactory<OnTrackGraph.Factory>().create(this)
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        val configuration = Configuration.Builder()
            .setWorkerFactory(DelegatingWorkerFactory(appGraph))
            .build()
        WorkManager.initialize(this, configuration)
        scheduleStationUpdates()
    }

    private fun scheduleStationUpdates() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val updateRequest = PeriodicWorkRequestBuilder<UpdateStationsWorker>(7, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "update_stations",
            ExistingPeriodicWorkPolicy.KEEP,
            updateRequest
        )
    }
}