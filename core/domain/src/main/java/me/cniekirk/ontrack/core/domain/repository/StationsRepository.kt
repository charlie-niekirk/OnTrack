package me.cniekirk.ontrack.core.domain.repository

import com.github.michaelbull.result.Result
import me.cniekirk.ontrack.core.domain.model.Station
import me.cniekirk.ontrack.core.domain.model.error.NetworkError

interface StationsRepository {

    suspend fun updateStations()

    suspend fun getStations(forceRefresh: Boolean = false): Result<List<Station>, NetworkError>
}