package com.nader.riyadalsalheen.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nader.riyadalsalheen.model.Hadith
import com.nader.riyadalsalheen.ui.components.HadithListItem

@Composable
fun HadithListScreen(hadiths: List<Hadith>, onHadithSelected: (Hadith) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(hadiths) { hadith ->
            HadithListItem(
                hadith = hadith,
                onClick = { onHadithSelected(hadith) },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}