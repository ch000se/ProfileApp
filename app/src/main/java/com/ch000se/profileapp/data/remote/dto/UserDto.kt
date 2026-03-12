package com.ch000se.profileapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    @SerialName("first_name")
    val name: String,
    @SerialName("last_name")
    val surname: String,
    val email: String,
    val avatar: String,
    val phone: String,
    @SerialName("dob")
    val dateOfBirthday: String
)
