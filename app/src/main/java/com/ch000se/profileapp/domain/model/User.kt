package com.ch000se.profileapp.domain.model

data class User(
    val name: String,
    val surname: String,
    val phone: String,
    val email: String,
    val dateOfBirthday: String,
    val avatar: String
)
