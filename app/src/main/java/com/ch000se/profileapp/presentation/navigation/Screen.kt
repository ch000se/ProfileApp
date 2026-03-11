package com.ch000se.profileapp.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {

    @Serializable
    data object Profile : Screen

    @Serializable
    data object CreateProfile : Screen

    @Serializable
    data object EditProfile : Screen
}