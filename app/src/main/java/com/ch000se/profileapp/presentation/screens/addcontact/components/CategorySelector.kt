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
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ch000se.profileapp.R
import com.ch000se.profileapp.core.presentation.mapper.asString
import com.ch000se.profileapp.domain.model.ContactCategory
import com.ch000se.profileapp.presentation.screens.addcontact.CategoryUiModel

@Composable
fun CategorySelector(
    categories: List<CategoryUiModel>,
    onCategoryToggle: (ContactCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.select_categories),
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { item ->
                key(item.category) {
                    FilterChip(
                        selected = item.isSelected,
                        onClick = { onCategoryToggle(item.category) },
                        label = { Text(text = item.label.asString()) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (item.category) {
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
}