package com.ch000se.profileapp.presentation.screens.contacts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ch000se.profileapp.R
import com.ch000se.profileapp.core_ui.model.asString
import com.ch000se.profileapp.presentation.preview.PreviewData
import com.ch000se.profileapp.presentation.screens.contacts.components.ContactItem
import com.ch000se.profileapp.presentation.screens.contacts.components.SearchBar
import com.ch000se.profileapp.ui.theme.ProfileAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    onNavigateToAddContact: () -> Unit,
    onNavigateToContactDetail: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LifecycleStartEffect(key1 = lifecycleOwner) {
        viewModel.onAction(ContactsUiAction.LoadContacts)
        onStopOrDispose {}
    }

    ContactsScreenContent(
        uiState = uiState,
        onNavigateToAddContact = onNavigateToAddContact,
        onNavigateToContactDetail = onNavigateToContactDetail,
        onNavigateToProfile = onNavigateToProfile,
        onSearchQuery = { viewModel.onAction(ContactsUiAction.SearchContacts(it)) },
        onToggleCategory = { viewModel.onAction(ContactsUiAction.ToggleCategoryFilter(it)) },
        onDeleteContact = { viewModel.onAction(ContactsUiAction.DeleteContact(it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactsScreenContent(
    uiState: ContactsUiState,
    onNavigateToAddContact: () -> Unit,
    onNavigateToContactDetail: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onSearchQuery: (String) -> Unit,
    onToggleCategory: (com.ch000se.profileapp.domain.model.ContactCategory) -> Unit,
    onDeleteContact: (String) -> Unit,
    initialSearchSectionExpanded: Boolean = false
) {
    var isSearchSectionExpanded by remember { mutableStateOf(initialSearchSectionExpanded) }

    val arrowRotation by animateFloatAsState(
        targetValue = if (isSearchSectionExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.contacts_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddContact,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_contact)
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isSearchSectionExpanded = !isSearchSectionExpanded }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.search_and_filters),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer { rotationZ = arrowRotation },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = isSearchSectionExpanded,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(
                    animationSpec = tween(durationMillis = 400)
                ),
                exit = shrinkVertically(
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(
                    animationSpec = tween(durationMillis = 200)
                )
            ) {
                Column {
                    SearchBar(
                        query = uiState.query,
                        onQueryChange = onSearchQuery,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.categoryFilters.forEach { filter ->
                            val category = filter.category
                            val isSelected = filter.isSelected
                            val label = filter.label
                            key(category) {
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onToggleCategory(category) },
                                    label = { Text(label.asString()) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.contacts.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_contacts),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.contacts,
                            key = { it.id }
                        ) { contact ->
                            val contactId = contact.id
                            ContactItem(
                                contact = contact,
                                onClick = { onNavigateToContactDetail(contactId) },
                                onDelete = { onDeleteContact(contactId) }
                            )
                        }
                    }
                }

                if (uiState.isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ContactsScreenPreview() {
    ProfileAppTheme {
        ContactsScreenContent(
            uiState = ContactsUiState(
                contacts = PreviewData.sampleContactsList,
                categoryFilters = PreviewData.sampleCategoryUiModels
            ),
            onNavigateToAddContact = {},
            onNavigateToContactDetail = {},
            onNavigateToProfile = {},
            onSearchQuery = {},
            onToggleCategory = {},
            onDeleteContact = {}
        )
    }
}

@Preview(name = "Empty", showBackground = true)
@Composable
private fun ContactsScreenEmptyPreview() {
    ProfileAppTheme {
        ContactsScreenContent(
            uiState = ContactsUiState(
                contacts = emptyList(),
                categoryFilters = emptyList()
            ),
            onNavigateToAddContact = {},
            onNavigateToContactDetail = {},
            onNavigateToProfile = {},
            onSearchQuery = {},
            onToggleCategory = {},
            onDeleteContact = {}
        )
    }
}

@Preview(name = "Deleting", showBackground = true)
@Composable
private fun ContactsScreenDeletingPreview() {
    ProfileAppTheme {
        ContactsScreenContent(
            uiState = ContactsUiState(
                contacts = PreviewData.sampleContactsList,
                categoryFilters = PreviewData.sampleCategoryUiModels,
                isDeleting = true
            ),
            onNavigateToAddContact = {},
            onNavigateToContactDetail = {},
            onNavigateToProfile = {},
            onSearchQuery = {},
            onToggleCategory = {},
            onDeleteContact = {}
        )
    }
}

@Preview(name = "With Search Query", showBackground = true)
@Composable
private fun ContactsScreenWithSearchPreview() {
    ProfileAppTheme {
        ContactsScreenContent(
            uiState = ContactsUiState(
                contacts = PreviewData.sampleContactsList.take(1),
                categoryFilters = PreviewData.sampleCategoryUiModels,
                query = "Alice"
            ),
            onNavigateToAddContact = {},
            onNavigateToContactDetail = {},
            onNavigateToProfile = {},
            onSearchQuery = {},
            onToggleCategory = {},
            onDeleteContact = {},
            initialSearchSectionExpanded = true
        )
    }
}