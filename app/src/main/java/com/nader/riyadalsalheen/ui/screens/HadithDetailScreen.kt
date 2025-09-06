package com.nader.riyadalsalheen.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nader.riyadalsalheen.model.Hadith
import com.nader.riyadalsalheen.ui.components.HtmlText

@Composable
fun HadithDetailScreen(hadith: Hadith) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        HtmlText(
            html = hadith.hadith,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        HtmlText(html = hadith.sharh)
    }
}