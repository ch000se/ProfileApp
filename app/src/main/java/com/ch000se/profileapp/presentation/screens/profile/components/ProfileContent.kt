package com.ch000se.profileapp.presentation.screens.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ch000se.profileapp.R
import com.ch000se.profileapp.domain.model.User
import com.ch000se.profileapp.core.presentation.components.ProfileAvatar
import com.ch000se.profileapp.core.presentation.components.ProfileInfoItem

@Composable
fun ProfileContent(
    user: User,
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            ProfileContentCompact(user = user, modifier = modifier)
        }

        else -> {
            ProfileContentExpanded(user = user, modifier = modifier)
        }
    }
}

@Composable
fun ProfileContentCompact(
    user: User,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        ProfileAvatar(avatarUri = user.avatar, size = 140)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${user.name} ${user.surname}",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProfileInfoItem(
                    icon = Icons.Default.Email,
                    label = stringResource(R.string.email_label),
                    value = user.email,
                    copyEnabled = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileInfoItem(
                    icon = Icons.Default.Phone,
                    label = stringResource(R.string.phone_label),
                    value = user.phone,
                    copyEnabled = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileInfoItem(
                    icon = Icons.Default.DateRange,
                    label = stringResource(R.string.date_of_birthday_label),
                    value = user.dateOfBirthday,
                    copyEnabled = true
                )
            }
        }
    }
}

@Composable
fun ProfileContentExpanded(
    user: User,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ProfileAvatar(avatarUri = user.avatar, size = 180)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "${user.name} ${user.surname}",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.width(48.dp))

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {
                ProfileInfoItem(
                    icon = Icons.Default.Email,
                    label = stringResource(R.string.email_label),
                    value = user.email,
                    copyEnabled = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                ProfileInfoItem(
                    icon = Icons.Default.Phone,
                    label = stringResource(R.string.phone_label),
                    value = user.phone,
                    copyEnabled = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                ProfileInfoItem(
                    icon = Icons.Default.DateRange,
                    label = stringResource(R.string.date_of_birthday_label),
                    value = user.dateOfBirthday,
                    copyEnabled = true
                )
            }
        }
    }
}