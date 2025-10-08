package me.cniekirk.ontrack.core.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import me.cniekirk.ontrack.core.domain.repository.StationsRepository
import timber.log.Timber

@Inject
class UpdateStationsWorker(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val stationsRepository: StationsRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            stationsRepository.updateStations()
            Result.success()
        } catch (exception: Exception) {
            Timber.e(exception)
            Result.failure()
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(appContext: Context, params: WorkerParameters): UpdateStationsWorker
    }
}