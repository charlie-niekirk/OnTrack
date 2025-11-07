package me.cniekirk.ontrack.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import me.cniekirk.ontrack.core.datastore.RecentSearches
import me.cniekirk.ontrack.core.datastore.RecentSearchesDataSource
import me.cniekirk.ontrack.core.datastore.RecentSearchesSerializer
import me.cniekirk.ontrack.core.di.components.ApplicationContext

private const val DATASTORE_FILE_NAME = "recent_searches.proto"

@BindingContainer
object DatastoreProviders {

    @Provides
    @SingleIn(AppScope::class)
    fun provideRecentSearchesDatastore(@ApplicationContext context: Context): DataStore<RecentSearches> {
        return DataStoreFactory.create(
            serializer = RecentSearchesSerializer,
            produceFile = { context.dataStoreFile(DATASTORE_FILE_NAME) },
            corruptionHandler = null,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideRecentSearchesDataSource(dataStore: DataStore<RecentSearches>): RecentSearchesDataSource =
        RecentSearchesDataSource(dataStore)
}