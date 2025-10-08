package me.cniekirk.ontrack.core.data.repository

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import me.cniekirk.ontrack.core.domain.repository.RealtimeTrainsRepository
import me.cniekirk.ontrack.core.network.api.RealtimeTrainsRemoteDataSource

@ContributesBinding(AppScope::class)
@Inject
internal class RealtimeTrainsRepositoryImpl(
    private val realtimeTrainsRemoteDataSource: RealtimeTrainsRemoteDataSource
) : RealtimeTrainsRepository {

    override suspend fun getDepartureBoard(
        station: String,
        year: String,
        month: String,
        day: String,
        time: String
    ): List<String> {

    }
}