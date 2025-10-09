package com.nader.riyadalsalheen.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign


@Composable
fun FontSizeDialog(
    fontSize: Float,
    onUpdateFontSize: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("حجم الخط") },
        text = {
            Column {
                Slider(
                    value = fontSize,
                    onValueChange = onUpdateFontSize,
                    valueRange = 14f..30f,
                    steps = 7
                )
                Text(
                    text = "الحجم: ${fontSize.toInt()}",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("حسناً")
            }
        }
    )
}