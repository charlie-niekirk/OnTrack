package me.cniekirk.ontrack.core.data.repository

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.cniekirk.ontrack.core.data.mapper.toDatastoreListRequest
import me.cniekirk.ontrack.core.data.mapper.toDomainModel
import me.cniekirk.ontrack.core.data.mapper.toLocalDataError
import me.cniekirk.ontrack.core.datastore.RecentSearchesDataSource
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceListRequest
import me.cniekirk.ontrack.core.domain.model.error.LocalDataError
import me.cniekirk.ontrack.core.domain.repository.RecentSearchesRepository

internal class RecentSearchesRepositoryImpl(
    private val recentSearchesDataSource: RecentSearchesDataSource
) : RecentSearchesRepository {

    override suspend fun cacheRecentSearch(serviceListRequest: ServiceListRequest): Result<Unit, LocalDataError> {
        val datastoreRequest = serviceListRequest.toDatastoreListRequest()
        return runCatching {
            recentSearchesDataSource.addRecentSearch(datastoreRequest)
        }.mapError(Throwable::toLocalDataError)
    }

    override suspend fun getRecentSearches(): Flow<List<ServiceListRequest>> {
        return recentSearchesDataSource.recentSearches
            .map { recentSearches ->
                recentSearches.recentSearchesList.map { it.toDomainModel() }
            }
    }

    override suspend fun deleteRecentSearch(serviceListRequest: ServiceListRequest): Result<Unit, LocalDataError> {
        val datastoreRequest = serviceListRequest.toDatastoreListRequest()
        return runCatching {
            recentSearchesDataSource.deleteRecentSearch(datastoreRequest)
        }.mapError(Throwable::toLocalDataError)
    }

    override suspend fun deleteAllRecentSearches(): Result<Unit, LocalDataError> {
        return runCatching {
            recentSearchesDataSource.deleteAllRecentSearches()
        }.mapError(Throwable::toLocalDataError)
    }
}