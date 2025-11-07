package me.cniekirk.ontrack.core.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow

class RecentSearchesDataSource(
    private val recentSearchesDataStore: DataStore<RecentSearches>
) {

    val recentSearches: Flow<RecentSearches> = recentSearchesDataStore.data

    suspend fun addRecentSearch(serviceListRequest: RecentSearches.ServiceListRequest) {
        recentSearchesDataStore.updateData { currentData ->
            // Check if this search already exists to avoid duplicates
            val existingSearches = currentData.recentSearchesList
            val isDuplicate = existingSearches.any { existing ->
                existing.serviceListType == serviceListRequest.serviceListType &&
                existing.targetStation.crs == serviceListRequest.targetStation.crs &&
                (!existing.hasFilterStation() && !serviceListRequest.hasFilterStation() ||
                 existing.hasFilterStation() && serviceListRequest.hasFilterStation() &&
                 existing.filterStation.crs == serviceListRequest.filterStation.crs)
            }

            if (isDuplicate) {
                // If it's a duplicate, remove the old one and add the new one at the beginning
                val filtered = existingSearches.filterNot { existing ->
                    existing.serviceListType == serviceListRequest.serviceListType &&
                    existing.targetStation.crs == serviceListRequest.targetStation.crs &&
                    (!existing.hasFilterStation() && !serviceListRequest.hasFilterStation() ||
                     existing.hasFilterStation() && serviceListRequest.hasFilterStation() &&
                     existing.filterStation.crs == serviceListRequest.filterStation.crs)
                }
                currentData.toBuilder()
                    .clearRecentSearches()
                    .addRecentSearches(serviceListRequest)
                    .addAllRecentSearches(filtered)
                    .build()
            } else {
                // Add new search at the beginning and limit to 10 recent searches
                val limitedSearches = if (existingSearches.size >= 10) {
                    existingSearches.dropLast(1)
                } else {
                    existingSearches
                }

                currentData.toBuilder()
                    .clearRecentSearches()
                    .addRecentSearches(serviceListRequest)
                    .addAllRecentSearches(limitedSearches)
                    .build()
            }
        }
    }

    suspend fun deleteRecentSearch(serviceListRequest: RecentSearches.ServiceListRequest) {
        recentSearchesDataStore.updateData { currentData ->
            val filtered = currentData.recentSearchesList.filterNot { existing ->
                existing.serviceListType == serviceListRequest.serviceListType &&
                existing.targetStation.crs == serviceListRequest.targetStation.crs &&
                (!existing.hasFilterStation() && !serviceListRequest.hasFilterStation() ||
                 existing.hasFilterStation() && serviceListRequest.hasFilterStation() &&
                 existing.filterStation.crs == serviceListRequest.filterStation.crs)
            }

            currentData.toBuilder()
                .clearRecentSearches()
                .addAllRecentSearches(filtered)
                .build()
        }
    }

    suspend fun deleteAllRecentSearches() {
        recentSearchesDataStore.updateData { currentData ->
            currentData.toBuilder()
                .clearRecentSearches()
                .build()
        }
    }
}