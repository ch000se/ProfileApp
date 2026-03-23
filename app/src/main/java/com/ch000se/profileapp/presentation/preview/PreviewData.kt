package com.ch000se.profileapp.presentation.preview

import com.ch000se.profileapp.R
import com.ch000se.profileapp.core_ui.model.UiText
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory
import com.ch000se.profileapp.domain.model.User
import com.ch000se.profileapp.domain.validation.UserField
import com.ch000se.profileapp.domain.validation.ValidationError
import com.ch000se.profileapp.presentation.common.model.CategoryUiModel
import com.ch000se.profileapp.presentation.screens.addcontact.AddContactUiState
import com.ch000se.profileapp.presentation.screens.addcontact.Selectable
import com.ch000se.profileapp.presentation.screens.edit.EditProfileUiState

object PreviewData {

    val sampleUser = User(
        name = "Svyatoslav",
        surname = "Chayka",
        phone = "+380501234567",
        email = "svyatoslav.@gmail.com",
        dateOfBirthday = "15.03.1990",
        avatar = ""
    )

    val sampleUserWithAvatar = sampleUser.copy(
        avatar = "https://randomuser.me/api/portraits/men/1.jpg"
    )

    val sampleContact = Contact(
        id = "1",
        name = "Svyatoslav",
        surname = "Chayka",
        phone = "+380501234567",
        email = "svyatoslav.@gmail.com",
        dateOfBirthday = "22.07.1995",
        avatar = "",
        categories = listOf(ContactCategory.FRIENDS)
    )

    val sampleContactWithAvatar = sampleContact.copy(
        avatar = "https://randomuser.me/api/portraits/women/1.jpg"
    )

    val sampleContactWithAllCategories = sampleContact.copy(
        categories = listOf(ContactCategory.FAMILY, ContactCategory.FRIENDS, ContactCategory.WORK)
    )

    val sampleContactsList = listOf(
        Contact(
            id = "1",
            name = "Roma",
            surname = "Alexandrov",
            phone = "+380501111111",
            email = "roma@gmail.com",
            dateOfBirthday = "10.01.1985",
            avatar = "",
            categories = listOf(ContactCategory.FAMILY)
        ),
        Contact(
            id = "2",
            name = "Andrew",
            surname = "Martin",
            phone = "+380502222222",
            email = "andrew@gmail.com",
            dateOfBirthday = "20.05.1992",
            avatar = "",
            categories = listOf(ContactCategory.WORK)
        ),
        Contact(
            id = "3",
            name = "Michael",
            surname = "Brown",
            phone = "+380503333333",
            email = "michael@gmail.com",
            dateOfBirthday = "15.11.1988",
            avatar = "",
            categories = listOf(ContactCategory.FRIENDS, ContactCategory.WORK)
        )
    )

    val sampleCategoryUiModels = listOf(
        CategoryUiModel(
            category = ContactCategory.FAMILY,
            label = UiText.StringResource(R.string.category_family),
            isSelected = false
        ),
        CategoryUiModel(
            category = ContactCategory.FRIENDS,
            label = UiText.StringResource(R.string.category_friends),
            isSelected = true
        ),
        CategoryUiModel(
            category = ContactCategory.WORK,
            label = UiText.StringResource(R.string.category_work),
            isSelected = false
        )
    )

    val sampleEditProfileUiState = EditProfileUiState(
        name = "Roma",
        surname = "Alexandrov",
        phone = "+380501111111",
        email = "roma@gmail.com",
        dateOfBirthday = "15.03.1990",
        avatarUri = "",
        validationErrors = emptyMap(),
        isLoading = false,
        showDatePicker = false,
        isSaveButtonEnabled = true
    )

    val sampleEditProfileUiStateEmpty = EditProfileUiState()

    val sampleEditProfileUiStateLoading = sampleEditProfileUiState.copy(
        isLoading = true
    )

    val sampleEditProfileUiStateWithErrors = EditProfileUiState(
        name = "",
        surname = "",
        phone = "invalid",
        email = "invalid-email",
        dateOfBirthday = "",
        avatarUri = "",
        validationErrors = mapOf(
            UserField.NAME to ValidationError.NameRequired,
            UserField.SURNAME to ValidationError.SurnameRequired,
            UserField.EMAIL to ValidationError.EmailInvalid,
            UserField.PHONE to ValidationError.PhoneInvalid
        ),
        isLoading = false,
        showDatePicker = false,
        isSaveButtonEnabled = false
    )

    val sampleAddContactUiState = AddContactUiState(
        isLoading = false,
        isLoadingMore = false,
        isSaving = false,
        isButtonEnabled = true,
        randomUsers = sampleContactsList.map { Selectable(it, false) },
        categories = sampleCategoryUiModels,
        error = null
    )

    val sampleAddContactUiStateWithSelection = sampleAddContactUiState.copy(
        randomUsers = sampleContactsList.mapIndexed { index, contact ->
            Selectable(contact, index == 0)
        }
    )

}
