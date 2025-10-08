package me.cniekirk.ontrack.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import me.cniekirk.ontrack.core.data.work.UpdateStationsWorker

class DelegatingWorkerFactory(private val onTrackGraph: OnTrackGraph) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            UpdateStationsWorker::class.java.name -> {
                onTrackGraph.updateStationsWorkerFactory.create(appContext, workerParameters)
            }
            else -> null
        }
    }
}