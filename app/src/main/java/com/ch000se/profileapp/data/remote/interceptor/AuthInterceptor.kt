package com.ch000se.profileapp.data.remote.interceptor

import com.ch000se.profileapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-Api-Key", BuildConfig.API_KEY)
            .build()
        return chain.proceed(request)
    }
}