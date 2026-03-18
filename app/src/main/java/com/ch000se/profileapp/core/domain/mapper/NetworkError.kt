package com.ch000se.profileapp.core.domain.mapper

import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

enum class NetworkError {
    REQUEST_TIMEOUT,
    NO_INTERNET,
    SERVER_ERROR,
    SERIALIZATION,
    UNKNOWN
}

fun Throwable?.toNetworkError(): NetworkError {
    return when (this) {
        is SocketTimeoutException -> NetworkError.REQUEST_TIMEOUT
        is UnknownHostException -> NetworkError.NO_INTERNET
        is ConnectException -> NetworkError.NO_INTERNET
        is SerializationException -> NetworkError.SERIALIZATION
        is HttpException -> {
            when (code()) {
                in 500..599 -> NetworkError.SERVER_ERROR
                else -> NetworkError.UNKNOWN
            }
        }

        else -> NetworkError.UNKNOWN
    }
}