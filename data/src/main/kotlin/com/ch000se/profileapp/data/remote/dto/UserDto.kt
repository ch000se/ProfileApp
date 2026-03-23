package com.ch000se.profileapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserDto(
    @SerialName("id")
    val id: String,
    @SerialName("first_name")
    val name: String,
    @SerialName("last_name")
    val surname: String,
    @SerialName("email")
    val email: String,
    @SerialName("avatar")
    val avatar: String,
    @SerialName("phone")
    val phone: String,
    @SerialName("dob")
    val dateOfBirthday: String
)
