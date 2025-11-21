package me.cniekirk.ontrack.testing.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import me.cniekirk.ontrack.core.domain.repository.RealtimeTrainsRepository
import me.cniekirk.ontrack.core.domain.repository.RecentSearchesRepository
import me.cniekirk.ontrack.core.domain.repository.StationsRepository
import me.cniekirk.ontrack.testing.fake.FakeRealtimeTrainsRepository
import me.cniekirk.ontrack.testing.fake.FakeRecentSearchesRepository
import me.cniekirk.ontrack.testing.fake.FakeStationsRepository

/**
 * Test binding container that provides fake implementations of repositories
 * for baseline profile testing. These fakes return hardcoded data immediately
 * without making real network calls.
 *
 * This container overrides the real repository bindings from DataProviders.
 */
@BindingContainer()
object TestBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    fun provideStationsRepository(): StationsRepository {
        return FakeStationsRepository()
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideRealtimeTrainsRepository(): RealtimeTrainsRepository {
        return FakeRealtimeTrainsRepository()
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideRecentSearchesRepository(): RecentSearchesRepository {
        return FakeRecentSearchesRepository()
    }
}
