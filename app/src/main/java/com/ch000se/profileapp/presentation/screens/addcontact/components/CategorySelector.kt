package com.ch000se.profileapp.presentation.screens.addcontact.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ch000se.profileapp.R
import com.ch000se.profileapp.domain.model.ContactCategory

@Composable
fun CategorySelector(
    selectedCategories: List<ContactCategory>,
    onCategoryToggle: (ContactCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.select_categories),
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ContactCategory.entries.forEach { category ->
                FilterChip(
                    selected = category in selectedCategories,
                    onClick = { onCategoryToggle(category) },
                    label = {
                        Text(
                            text = when (category) {
                                ContactCategory.FAMILY -> stringResource(R.string.category_family)
                                ContactCategory.FRIENDS -> stringResource(R.string.category_friends)
                                ContactCategory.WORK -> stringResource(R.string.category_work)
                            }
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when (category) {
                            ContactCategory.FAMILY -> MaterialTheme.colorScheme.tertiaryContainer
                            ContactCategory.FRIENDS -> MaterialTheme.colorScheme.secondaryContainer
                            ContactCategory.WORK -> MaterialTheme.colorScheme.primaryContainer
                        }
                    )
                )
            }
        }
    }
}