package com.nader.riyadalsalheen.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.nader.riyadalsalheen.model.Door
import com.nader.riyadalsalheen.ui.viewmodel.MainViewModel

@Composable
fun DoorListScreen(viewModel: MainViewModel, onDoorSelected: (Door) -> Unit) {
    if (viewModel.currentBookDoors.value.isEmpty()) {
        Text(
            text = "جاري التحميل...",
            modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(viewModel.currentBookDoors.value) { door ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = door.title,
                            textAlign = TextAlign.End,
                            fontSize = 18.sp
                        )
                    },
                    modifier = Modifier.clickable {
                        onDoorSelected(door)
                    }
                )
            }
        }
    }
}