package com.ch000se.profileapp.presentation.screens.profile.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.ch000se.profileapp.domain.model.User

internal object ProfilePreviewData {

    val sampleUser = User(
        name = "Svyatoslav",
        surname = "Chayka",
        phone = "+380501234567",
        email = "svyatoslav@gmail.com",
        dateOfBirthday = "15.03.1990",
        avatar = ""
    )

    val sampleUserWithAvatar = sampleUser.copy(
        avatar = "https://randomuser.me/api/portraits/men/1.jpg"
    )
}

internal class UserPreviewProvider : PreviewParameterProvider<User> {
    override val values = sequenceOf(
        ProfilePreviewData.sampleUser,
        ProfilePreviewData.sampleUserWithAvatar
    )
}