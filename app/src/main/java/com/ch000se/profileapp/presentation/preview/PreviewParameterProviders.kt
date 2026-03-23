package com.ch000se.profileapp.presentation.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.User

class UserPreviewProvider : PreviewParameterProvider<User> {
    override val values = sequenceOf(
        PreviewData.sampleUser,
        PreviewData.sampleUserWithAvatar
    )
}

class ContactPreviewProvider : PreviewParameterProvider<Contact> {
    override val values = sequenceOf(
        PreviewData.sampleContact,
        PreviewData.sampleContactWithAvatar,
        PreviewData.sampleContactWithAllCategories
    )
}
