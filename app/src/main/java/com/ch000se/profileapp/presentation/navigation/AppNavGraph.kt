package com.ch000se.profileapp.presentation.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.ch000se.profileapp.presentation.screens.edit.EditProfileScreen
import com.ch000se.profileapp.presentation.screens.profile.ProfileScreen

@Composable
fun AppNavGraph(
    windowSize: WindowWidthSizeClass,
    startScreen: NavKey
) {
    val backStack = rememberNavBackStack(startScreen)

    NavDisplay(
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Screen.Profile> {
                ProfileScreen(
                    windowSize = windowSize,
                    onNavigateToEdit = {
                        backStack.add(Screen.EditProfile)
                    },
                    onNavigateToCreate = {
                        backStack.removeLastOrNull()
                        backStack.add(Screen.CreateProfile)
                    }
                )
            }

            entry<Screen.CreateProfile> {
                EditProfileScreen(
                    windowSize = windowSize,
                    isCreateMode = true,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                        backStack.add(Screen.Profile)
                    }
                )
            }

            entry<Screen.EditProfile> {
                EditProfileScreen(
                    windowSize = windowSize,
                    isCreateMode = false,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}