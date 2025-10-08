package me.cniekirk.ontrack.core.network.interceptor

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

internal class BasicAuthInterceptor(
    private val username: String,
    private val password: String
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val credentials = Credentials.basic(username, password)
        val request = chain.request().newBuilder()
            .addHeader("Authorization", credentials)
            .build()
        return chain.proceed(request)
    }
}