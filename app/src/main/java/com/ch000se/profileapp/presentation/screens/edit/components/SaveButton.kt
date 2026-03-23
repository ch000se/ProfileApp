package com.ch000se.profileapp.presentation.screens.edit.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ch000se.profileapp.R
import com.ch000se.profileapp.ui.theme.ProfileAppTheme

@Composable
fun SaveButton(
    isLoading: Boolean,
    isEnabled: Boolean,
    isCreateMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = isEnabled && !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(stringResource(if (isCreateMode) R.string.create else R.string.save))
        }
    }
}

@PreviewLightDark
@Composable
private fun SaveButtonPreview() {
    ProfileAppTheme {
        Surface {
            SaveButton(
                isLoading = false,
                isEnabled = true,
                isCreateMode = false,
                onClick = {},
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }
    }
}

@Preview(name = "All States", showBackground = true)
@Composable
private fun SaveButtonAllStatesPreview() {
    ProfileAppTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                SaveButton(
                    isLoading = false,
                    isEnabled = true,
                    isCreateMode = false,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                SaveButton(
                    isLoading = false,
                    isEnabled = false,
                    isCreateMode = false,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                SaveButton(
                    isLoading = true,
                    isEnabled = true,
                    isCreateMode = true,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
