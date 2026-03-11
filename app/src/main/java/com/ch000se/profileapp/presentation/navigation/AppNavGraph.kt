package com.ch000se.profileapp.presentation.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ch000se.profileapp.presentation.edit.EditProfileScreen
import com.ch000se.profileapp.presentation.profile.ProfileScreen

@Composable
fun AppNavGraph(
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Screen = Screen.Profile
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Screen.Profile> {
            ProfileScreen(
                windowSize = windowSize,
                onNavigateToEdit = {
                    navController.navigate(Screen.EditProfile)
                }
            )
        }

        composable<Screen.CreateProfile> {
            EditProfileScreen(
                windowSize = windowSize,
                isCreateMode = true,
                onNavigateBack = {
                    navController.navigate(Screen.Profile) {
                        popUpTo(Screen.CreateProfile) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.EditProfile> {
            EditProfileScreen(
                windowSize = windowSize,
                isCreateMode = false,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}