package com.nader.riyadalsalheen.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nader.riyadalsalheen.R
import com.nader.riyadalsalheen.ui.theme.RiyadalsalheenTheme
import com.nader.riyadalsalheen.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

data class HomeScreenUiState(
    val isDarkTheme: Boolean = false,
    val lastHadithId: Int = 0,
    val bookmarksCount: Int = 0,
    val booksCount: Int = 0,
    val versionName: String = "1.0.0"
)

@Composable
fun  HomeScreen(viewModel: MainViewModel,
                onBooksClicked: () -> Unit,
                onNavigateToHadith: (hadithId: Int) -> Unit,
                onBookmarksClicked: () -> Unit,
                onSearchClicked: () -> Unit
) {
    // Collect state from ViewModel
    val coroutineScope = rememberCoroutineScope()
    val currentHadith = viewModel.currentHadith.value
    // Create UI state
    val uiState = HomeScreenUiState(
        isDarkTheme = viewModel.isDarkTheme.value,
        lastHadithId = currentHadith?.hadith?.id ?: 0,
        bookmarksCount = viewModel.bookmarks.value.size,
        booksCount = viewModel.books.value.size,
        versionName = viewModel.packageInfo.versionName ?: "1.0.0"
    )

    // Delegate to the pure UI composable
    HomeScreenContent(
        uiState = uiState,
        onBooksClicked = onBooksClicked,
        onNavigateToHadith = onNavigateToHadith,
        onBookmarksClicked = onBookmarksClicked,
        onSearchClicked = onSearchClicked,
        onToggleDarkTheme = { viewModel.toggleDarkTheme() },
        onRandomHadithClicked = {
            coroutineScope.launch {
//                val randomHadith = viewModel.getRandomHadith()
//                randomHadith?.let {
//                    onNavigateToHadith(it.id)
//                }
            }
        }
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeScreenUiState,
    onBooksClicked: () -> Unit = {},
    onNavigateToHadith: (hadithId: Int) -> Unit = {},
    onBookmarksClicked: () -> Unit = {},
    onSearchClicked: () -> Unit = {},
    onToggleDarkTheme: () -> Unit = {},
    onRandomHadithClicked: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (uiState.isDarkTheme) {
                            listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                            )
                        } else {
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.surface
                            )
                        }
                    )
                )
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // App Title with Animation
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = "رياض الصالحين",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "من كلام سيد المرسلين",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionCard(
                    icon = ImageVector.vectorResource(id = R.drawable.ic_book_24),
                    title = if (uiState.lastHadithId > 1) "متابعة القراءة" else "ابدأ القراءة",
                    subtitle = if (uiState.lastHadithId > 1) "الحديث ${uiState.lastHadithId}" else "",
                    onClick = {
                        onNavigateToHadith(uiState.lastHadithId)
                    },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                QuickActionCard(
                    icon = ImageVector.vectorResource(id = R.drawable.ic_bookmarks_24),
                    title = "المفضلة",
                    subtitle = "${uiState.bookmarksCount} حديث",
                    onClick = onBookmarksClicked,
                    modifier = Modifier.weight(1f)
                )

            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Actions Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionCard(
                    icon = ImageVector.vectorResource(id = R.drawable.ic_menu_book_24),
                    title = "استعراض الكتاب",
                    subtitle = "${uiState.booksCount} كتاب",
                    onClick = onBooksClicked,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                QuickActionCard(
                    icon = ImageVector.vectorResource(id = R.drawable.ic_search_24),
                    title = "البحث",
                    subtitle = "بحث سريع",
                    onClick = onSearchClicked,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Daily Hadith Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_auto_awesome_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "حديث اليوم",
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onRandomHadithClicked,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("اقرأ حديث عشوائي")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Settings Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = if (uiState.isDarkTheme) R.drawable.ic_dark_mode_24 else R.drawable.ic_light_mode_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (uiState.isDarkTheme) "الوضع الليلي" else "الوضع النهاري",
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Switch(
                        checked = uiState.isDarkTheme,
                        onCheckedChange = { onToggleDarkTheme() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Version Info
            Text(
                text = "إصدار التطبيق: ${uiState.versionName}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
        }


    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(
    name = "Default Light Theme",
    showBackground = true,
    locale = "ar"
)
@Composable
fun HomeScreenContentPreview() {
    RiyadalsalheenTheme {
        HomeScreenContent(
            uiState = HomeScreenUiState(
                isDarkTheme = false,
                lastHadithId = 5,
                bookmarksCount = 3,
                booksCount = 11,
                versionName = "1.0.0"
            )
        )
    }
}

@Preview(
    name = "Dark Theme",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    locale = "ar"
)
@Composable
fun HomeScreenContentDarkPreview() {
    RiyadalsalheenTheme {
        HomeScreenContent(
            uiState = HomeScreenUiState(
                isDarkTheme = true,
                lastHadithId = 42,
                bookmarksCount = 15,
                booksCount = 22,
                versionName = "1.0.0"
            )
        )
    }
}
