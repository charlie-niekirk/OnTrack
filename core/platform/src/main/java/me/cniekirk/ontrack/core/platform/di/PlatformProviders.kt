package me.cniekirk.ontrack.core.platform.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import me.cniekirk.ontrack.core.platform.LocalTimeProvider
import me.cniekirk.ontrack.core.platform.TimeProvider

@BindingContainer
object PlatformProviders {

    @Provides
    @SingleIn(AppScope::class)
    fun provideTimeProvider(): TimeProvider = LocalTimeProvider()
}