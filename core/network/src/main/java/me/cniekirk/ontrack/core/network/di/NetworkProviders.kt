package me.cniekirk.ontrack.core.network.di

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import me.cniekirk.ontrack.core.di.components.ApplicationContext
import me.cniekirk.ontrack.core.network.api.openraildata.OpenRailDataApi
import me.cniekirk.ontrack.core.network.api.realtimetrains.RealtimeTrainsApi
import me.cniekirk.ontrack.core.network.interceptor.BasicAuthInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import kotlin.time.Duration.Companion.seconds

private const val RTT_USERNAME = "rttapi_cniekirk"
private const val RTT_PASSWORD = "e74893841ebdb96fe035ab783d044ffbb8b4d70e"
private const val RTT_BASE_URL = "https://api.rtt.io"

private const val OPEN_RAIL_USERNAME = "cniekirk@gmail.com"
private const val OPEN_RAIL_PASSWORD = "WA9qNc!ZP5CHjzu"
private const val OPEN_RAIL_BASE_URL = "https://publicdatafeeds.networkrail.co.uk/"

@ContributesTo(AppScope::class)
interface NetworkProviders {

    @Provides
    @SingleIn(AppScope::class)
    fun provideCache(@ApplicationContext context: Context): Cache {
        return Cache(context.cacheDir, 10 * 10 * 1024)
    }

    @Named("rtt-okhttp")
    @Provides
    @SingleIn(AppScope::class)
    fun provideRttOkHttpClient(cache: Cache): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(BasicAuthInterceptor(RTT_USERNAME, RTT_PASSWORD))
            .callTimeout(30.seconds)
            .readTimeout(30.seconds)
            .writeTimeout(30.seconds)
            .connectTimeout(5.seconds)
            .build()
    }

    @Named("open-rail-okhttp")
    @Provides
    @SingleIn(AppScope::class)
    fun provideOpenRailOkHttpClient(cache: Cache): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(BasicAuthInterceptor(OPEN_RAIL_USERNAME, OPEN_RAIL_PASSWORD))
            .callTimeout(30.seconds)
            .readTimeout(30.seconds)
            .writeTimeout(30.seconds)
            .connectTimeout(5.seconds)
            .build()
    }

    @Named("rtt-retrofit")
    @Provides
    @SingleIn(AppScope::class)
    fun provideRttRetrofit(@Named("rtt-okhttp") okHttpClient: Lazy<OkHttpClient>): Retrofit {
        return Retrofit.Builder()
            .callFactory { okHttpClient.value.newCall(it) }
            .baseUrl(RTT_BASE_URL)
            .build()
    }

    @Named("open-rail-retrofit")
    @Provides
    @SingleIn(AppScope::class)
    fun provideOpenRailRetrofit(@Named("open-rail-okhttp") okHttpClient: Lazy<OkHttpClient>): Retrofit {
        return Retrofit.Builder()
            .callFactory { okHttpClient.value.newCall(it) }
            .baseUrl(OPEN_RAIL_BASE_URL)
            .build()
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideRealtimeTrainsApi(@Named("rtt-retrofit") retrofit: Retrofit): RealtimeTrainsApi =
        retrofit.create(RealtimeTrainsApi::class.java)

    @Provides
    @SingleIn(AppScope::class)
    fun provideOpenRailDataApi(@Named("open-rail-retrofit") retrofit: Retrofit): OpenRailDataApi =
        retrofit.create(OpenRailDataApi::class.java)
}