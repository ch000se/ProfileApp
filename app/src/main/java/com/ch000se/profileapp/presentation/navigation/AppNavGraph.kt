package com.ch000se.profileapp.presentation.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.ch000se.profileapp.presentation.screens.addcontact.AddContactScreen
import com.ch000se.profileapp.presentation.screens.contactDetail.ContactDetailScreen
import com.ch000se.profileapp.presentation.screens.contacts.ContactsScreen
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
        onBack = dropUnlessResumed {
            if (backStack.size > 1) backStack.removeLastOrNull()
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Screen.Profile> {
                ProfileScreen(
                    windowSize = windowSize,
                    onNavigateToEdit = dropUnlessResumed {
                        backStack.add(Screen.EditProfile)
                    },
                    onNavigateBack = dropUnlessResumed {
                        if (backStack.size > 1) backStack.removeLastOrNull()
                    },
                    onLogout = dropUnlessResumed {
                        backStack.clear()
                        backStack.add(Screen.CreateProfile)
                    }
                )
            }

            entry<Screen.CreateProfile> {
                EditProfileScreen(
                    windowSize = windowSize,
                    isCreateMode = true,
                    onNavigateBack = dropUnlessResumed {
                        backStack.removeLastOrNull()
                        backStack.add(Screen.Contacts)
                    }
                )
            }

            entry<Screen.EditProfile> {
                EditProfileScreen(
                    windowSize = windowSize,
                    isCreateMode = false,
                    onNavigateBack = dropUnlessResumed {
                        if (backStack.size > 1) backStack.removeLastOrNull()
                    }
                )
            }

            entry<Screen.Contacts> {
                ContactsScreen(
                    onNavigateToAddContact = dropUnlessResumed {
                        backStack.add(Screen.AddContact)
                    },
                    onNavigateToProfile = dropUnlessResumed {
                        backStack.add(Screen.Profile)
                    },
                    onNavigateToContactDetail = { contactId ->
                        if (backStack.last() is Screen.Contacts) {
                            backStack.add(Screen.ContactDetails(contactId))
                        }
                    },
                )
            }

            entry<Screen.AddContact> {
                AddContactScreen(
                    windowSize = windowSize,
                    onNavigateBack = dropUnlessResumed {
                        if (backStack.size > 1) backStack.removeLastOrNull()
                    }
                )
            }

            entry<Screen.ContactDetails> { contact ->
                ContactDetailScreen(
                    windowSize = windowSize,
                    contactId = contact.contactId,
                    onNavigateBack = dropUnlessResumed {
                        if (backStack.size > 1) backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}