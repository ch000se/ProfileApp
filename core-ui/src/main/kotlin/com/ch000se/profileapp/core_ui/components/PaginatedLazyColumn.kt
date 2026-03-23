package com.ch000se.profileapp.core_ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private const val PREFETCH_DISTANCE = 10

@Composable
fun <T> PaginatedLazyColumn(
    items: List<T>,
    key: (T) -> Any,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    itemContent: @Composable LazyItemScope.(index: Int, item: T) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> key(item) }
        ) { index, item ->
            itemContent(index, item)

            if (index == items.size - PREFETCH_DISTANCE && !isLoadingMore) {
                LaunchedEffect(items.size) {
                    onLoadMore()
                }
            }
        }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Preview(name = "With Items", showBackground = true)
@Composable
private fun PaginatedLazyColumnWithItemsPreview() {
    val sampleItems = (1..10).map { "Item $it" }
    MaterialTheme {
        PaginatedLazyColumn(
            items = sampleItems,
            key = { it },
            isLoadingMore = false,
            onLoadMore = {},
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) { _, item ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(text = item, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Preview(name = "Loading More", showBackground = true)
@Composable
private fun PaginatedLazyColumnLoadingPreview() {
    val sampleItems = (1..5).map { "Item $it" }
    MaterialTheme {
        PaginatedLazyColumn(
            items = sampleItems,
            key = { it },
            isLoadingMore = true,
            onLoadMore = {},
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) { _, item ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(text = item, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Preview(name = "Empty List", showBackground = true)
@Composable
private fun PaginatedLazyColumnEmptyPreview() {
    MaterialTheme {
        PaginatedLazyColumn(
            items = emptyList<String>(),
            key = { it },
            isLoadingMore = false,
            onLoadMore = {},
            modifier = Modifier.fillMaxSize()
        ) { _, item ->
            Text(text = item)
        }
    }
}
