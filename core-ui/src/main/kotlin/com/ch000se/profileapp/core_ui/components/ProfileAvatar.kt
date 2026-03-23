package com.ch000se.profileapp.core_ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun ProfileAvatar(
    avatarUri: String,
    modifier: Modifier = Modifier,
    size: Int = 120
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .border(
                width = 3.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (avatarUri.isNotEmpty()) {
            AsyncImage(
                model = avatarUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size((size / 2).dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private class AvatarSizeProvider : PreviewParameterProvider<Int> {
    override val values = sequenceOf(80, 120, 180)
}

@PreviewLightDark
@Composable
private fun ProfileAvatarPlaceholderPreview() {
    MaterialTheme {
        ProfileAvatar(
            avatarUri = "",
            size = 120
        )
    }
}

@Preview(name = "Different Sizes")
@Composable
private fun ProfileAvatarSizesPreview(
    @PreviewParameter(AvatarSizeProvider::class) size: Int
) {
    MaterialTheme {
        ProfileAvatar(
            avatarUri = "",
            size = size
        )
    }
}

@Preview(name = "With Avatar URL", showBackground = true)
@Composable
private fun ProfileAvatarWithImagePreview() {
    MaterialTheme {
        ProfileAvatar(
            avatarUri = "https://randomuser.me/api/portraits/men/1.jpg",
            size = 120
        )
    }
}
