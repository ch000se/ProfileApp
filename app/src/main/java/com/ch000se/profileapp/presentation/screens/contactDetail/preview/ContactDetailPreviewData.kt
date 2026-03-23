package com.ch000se.profileapp.presentation.screens.contactDetail.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory

internal object ContactDetailPreviewData {

    val sampleContact = Contact(
        id = "1",
        name = "Svyatoslav",
        surname = "Chayka",
        phone = "+380501234567",
        email = "svyatoslav@gmail.com",
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
}

internal class ContactDetailPreviewProvider : PreviewParameterProvider<Contact> {
    override val values = sequenceOf(
        ContactDetailPreviewData.sampleContact,
        ContactDetailPreviewData.sampleContactWithAvatar,
        ContactDetailPreviewData.sampleContactWithAllCategories
    )
}
