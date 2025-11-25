package me.cniekirk.ontrack.core.domain.repository

import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow
import me.cniekirk.ontrack.core.domain.model.arguments.ServiceListRequest
import me.cniekirk.ontrack.core.domain.model.error.LocalDataError

interface RecentSearchesRepository {

    suspend fun cacheRecentSearch(serviceListRequest: ServiceListRequest): Result<Unit, LocalDataError>

    suspend fun getRecentSearches(): Flow<List<ServiceListRequest>>

    suspend fun deleteRecentSearch(serviceListRequest: ServiceListRequest): Result<Unit, LocalDataError>

    suspend fun deleteAllRecentSearches(): Result<Unit, LocalDataError>
}