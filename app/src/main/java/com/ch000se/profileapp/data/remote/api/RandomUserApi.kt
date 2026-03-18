package com.ch000se.ninjauser.data.remote

import com.ch000se.profileapp.data.remote.dto.UserDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserApi {

    @GET("v2/randomuser")
    suspend fun getUsers(
        @Query("count") count: Int
    ): List<UserDto>
}