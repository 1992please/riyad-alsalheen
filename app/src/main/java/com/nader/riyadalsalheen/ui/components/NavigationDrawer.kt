package com.nader.riyadalsalheen.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nader.riyadalsalheen.R
import com.nader.riyadalsalheen.model.Hadith
import com.nader.riyadalsalheen.ui.theme.LocalDarkTheme

@Composable
fun NavigationDrawer(
    bookmarks: List<Hadith>,
    hadithCount: Int,
    onNavigateToBookmarks: () -> Unit = {},
    onNavigateToHadith: (Int) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onFontSizeChange: () -> Unit = {},
    onToggleDarkMode: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    val isDarkMode = LocalDarkTheme.current
    ModalDrawerSheet(
        modifier = Modifier.fillMaxWidth(0.85f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 12.dp)
            )
            {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = "رياض الصالحين",
                        fontSize = 22.sp,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$hadithCount حديث",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Column {
                    IconButton(onClick = onToggleDarkMode) {
                        Icon(
                            imageVector = ImageVector.vectorResource(if (isDarkMode) R.drawable.ic_dark_mode_24 else R.drawable.ic_light_mode_24),
                            contentDescription = if (isDarkMode) "Switch to System Mode" else "Switch to Dark Mode"
                        )
                    }

                    IconButton(onClick = {
                        onFontSizeChange()
                        onClose()
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_text_fields_24),
                            contentDescription = "حجم الخط"
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(8.dp)
            ) {
                // Divider
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Bookmarks Section
                item {
                    DrawerMenuItem(
                        text = "العلامات المرجعية",
                        icon = ImageVector.vectorResource(R.drawable.ic_bookmark_24)
                    )
                }


                // Recent Bookmarks
                items(bookmarks.take(3)) { bookmark ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 0.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
                            .clickable {
                                onNavigateToHadith(bookmark.id)
                                onClose()
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${bookmark.id}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.End,
                            modifier = Modifier.width(40.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = bookmark.title,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // View All Bookmarks
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp) // Ensure minimum touch target size (>44dp) [1]
                            .clickable {
                                onNavigateToBookmarks()
                                onClose()
                            }
                            .padding(start = 24.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_forward_24),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp), // Slightly smaller icon for a sub-item
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f) // Duller tint
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "عرض جميع العلامات المرجعية",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Divider
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Settings
                item {
                    DrawerMenuItem(
                        text = "الإعدادات",
                        icon = ImageVector.vectorResource(R.drawable.ic_settings_24),
                        modifier = Modifier.clickable{
                            onNavigateToSettings()
                            onClose()
                        }
                    )
                }

                // About
                item {
                    DrawerMenuItem(
                        text = "حول التطبيق",
                        icon = ImageVector.vectorResource(R.drawable.ic_info_24),
                        modifier = Modifier.clickable{
                            onNavigateToAbout()
                            onClose()
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun DrawerMenuItem(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}