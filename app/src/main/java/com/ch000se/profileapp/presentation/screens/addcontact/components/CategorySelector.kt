package com.ch000se.profileapp.presentation.screens.addcontact.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ch000se.profileapp.R
import com.ch000se.profileapp.presentation.common.model.CategoryUiModel
import com.ch000se.profileapp.core_ui.model.UiText
import com.ch000se.profileapp.core_ui.model.asString
import com.ch000se.profileapp.domain.model.ContactCategory
import com.ch000se.profileapp.presentation.screens.addcontact.preview.AddContactPreviewData
import com.ch000se.profileapp.ui.theme.ProfileAppTheme

@Composable
fun CategorySelector(
    categories: List<CategoryUiModel>,
    onCategoryToggle: (ContactCategory) -> Unit,
    modifier: Modifier = Modifier
) {

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
                val category = item.category
                val isSelected = item.isSelected
                val label = item.label
                key(category) {
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategoryToggle(category) },
                        label = { Text(text = label.asString()) },
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
}

@PreviewLightDark
@Composable
private fun CategorySelectorPreview() {
    ProfileAppTheme {
        Surface {
            CategorySelector(
                categories = AddContactPreviewData.sampleCategoryUiModels,
                onCategoryToggle = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "All Selected", showBackground = true)
@Composable
private fun CategorySelectorAllSelectedPreview() {
    val categories = listOf(
        CategoryUiModel(ContactCategory.FAMILY, UiText.StringResource(R.string.category_family), true),
        CategoryUiModel(ContactCategory.FRIENDS, UiText.StringResource(R.string.category_friends), true),
        CategoryUiModel(ContactCategory.WORK, UiText.StringResource(R.string.category_work), true)
    )
    ProfileAppTheme {
        Surface {
            CategorySelector(
                categories = categories,
                onCategoryToggle = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}