package com.ch000se.profileapp.domain.model

data class Contact(
    val id: String,
    val name: String,
    val surname: String,
    val phone: String,
    val email: String,
    val dateOfBirthday: String,
    val avatar: String,
    val categories: List<ContactCategory>
)
