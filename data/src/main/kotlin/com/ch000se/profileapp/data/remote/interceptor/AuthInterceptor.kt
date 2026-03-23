package com.ch000se.profileapp.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response

internal class AuthInterceptor(
    private val apiKey: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-Api-Key", apiKey)
            .build()
        return chain.proceed(request)
    }
}
