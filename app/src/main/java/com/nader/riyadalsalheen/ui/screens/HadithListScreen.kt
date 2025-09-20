package com.nader.riyadalsalheen.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nader.riyadalsalheen.model.Hadith
import com.nader.riyadalsalheen.ui.components.HadithListItem
import com.nader.riyadalsalheen.ui.viewmodel.MainViewModel

@Composable
fun HadithListScreen(viewModel: MainViewModel, onHadithSelected: (Hadith) -> Unit) {
    if (viewModel.currentDoorHadiths.value.isEmpty()) {
        Text(
            text = "جاري التحميل...",
            modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
    }
    else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(viewModel.currentDoorHadiths.value) { hadith ->
                HadithListItem(
                    hadith = hadith,
                    onClick = { onHadithSelected(hadith) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}