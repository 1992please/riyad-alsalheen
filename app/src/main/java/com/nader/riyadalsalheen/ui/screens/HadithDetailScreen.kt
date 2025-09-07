package com.nader.riyadalsalheen.ui.screens

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nader.riyadalsalheen.ui.components.HtmlText
import com.nader.riyadalsalheen.ui.viewmodel.MainViewModel

@Composable
fun HadithDetailScreen(
    viewModel: MainViewModel,
    onNextHadith: () -> Unit,
    onPreviousHadith: () -> Unit,
    onBack: () -> Unit
) {
    val currentHadith = viewModel.hadith.value
    if(currentHadith == null) {
        Text(
            text = "جاري التحميل...",
            modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
    }
    else {
        val currentBook = viewModel.books.value.find { currentHadith.bookId == it.id }
        val currentDoor = viewModel.doors.value.find { currentHadith.doorId == it.id }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .pointerInput(Unit){
                    detectHorizontalDragGestures { change, dragAmount ->
                        when {
                            dragAmount > 50 -> onNextHadith() // Swipe right
                            dragAmount < -50 -> onPreviousHadith() // Swipe left
                        }
                    }
                }
        ) {
            // Back button
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            if(currentBook != null && currentDoor != null) {
                Row (
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        text = currentDoor.title,
                        textAlign = TextAlign.Start,
                        fontSize = 10.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "الحديث ${currentHadith.id}",
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = currentBook.title,
                        textAlign = TextAlign.End,
                        fontSize = 10.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HtmlText(
                html = currentHadith.hadith,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            HtmlText(html = currentHadith.sharh)
        }
    }
}