package com.ch000se.profileapp.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey {

    @Serializable
    data object Profile : Screen

    @Serializable
    data object CreateProfile : Screen

    @Serializable
    data object EditProfile : Screen

    @Serializable
    data object Contacts : Screen

    @Serializable
    data object AddContact : Screen

    @Serializable
    data class ContactDetails(val contactId: String) : Screen
}