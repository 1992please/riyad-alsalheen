package com.nader.riyadalsalheen.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nader.riyadalsalheen.ui.viewmodel.MainViewModel

@Composable
fun  HomeScreen(viewModel: MainViewModel,
                onBooksClicked: () -> Unit,
                onNavigateToHadith: (hadithId: Int) -> Unit
) {
    // Get app version
    Column(
    modifier = Modifier
    .fillMaxSize()
    .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "رياض الصالحين",
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Go to books button
        Button(
            onClick = onBooksClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "استعراض الكتاب")
        }

        var isDarkTheme by remember { mutableStateOf(false) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(text = "الوضع الليلي")
            Switch(
                checked = isDarkTheme,
                onCheckedChange = {
                    isDarkTheme = it
                    // Implement theme change logic here
                    // You'll need to implement theme persistence
                }
            )
        }

        val versionName = viewModel.packageInfo.versionName
        Text(
            text = "إصدار التطبيق: $versionName",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 32.dp)
        )
    }
}