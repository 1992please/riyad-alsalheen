package com.nader.riyadalsalheen.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nader.riyadalsalheen.model.Hadith

@Composable
fun HadithListItem(hadith: Hadith, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card (
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(4.dp)
    ){
        Column(Modifier.padding(16.dp)) {
            Text(
                text = hadith.title,
                textAlign = TextAlign.End,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "الحديث ${hadith.id}",
                textAlign = TextAlign.End,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}