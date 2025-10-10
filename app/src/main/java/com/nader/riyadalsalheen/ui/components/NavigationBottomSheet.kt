package com.nader.riyadalsalheen.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nader.riyadalsalheen.model.Book
import com.nader.riyadalsalheen.model.Door

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBottomSheet(
    books: List<Book>,
    doors: List<Door>,
    onNavigateToDoor: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedBook by remember { mutableStateOf<Book?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Books List
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(books) { book ->
                    ListItem(
                        headlineContent = {
                            Text(
                                text = book.title,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        modifier = Modifier.clickable { selectedBook = book }
                    )
                }
            }

            // Doors List
            selectedBook?.let { book ->
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    val filteredDoors = doors.filter { it.bookId == book.id }
                    items(filteredDoors) { door ->
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = door.title,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            modifier = Modifier.clickable {
                                onNavigateToDoor(door.id)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}