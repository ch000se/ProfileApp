package com.ch000se.profileapp.data.mapper

import com.ch000se.profileapp.core.error.NetworkError
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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
