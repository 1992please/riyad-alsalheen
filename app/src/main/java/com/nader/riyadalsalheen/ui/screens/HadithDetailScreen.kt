package com.nader.riyadalsalheen.ui.screens

import android.content.ClipData
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nader.riyadalsalheen.R
import com.nader.riyadalsalheen.model.Book
import com.nader.riyadalsalheen.model.Door
import com.nader.riyadalsalheen.model.Hadith
import com.nader.riyadalsalheen.model.HadithDetails
import com.nader.riyadalsalheen.ui.components.HtmlText
import com.nader.riyadalsalheen.ui.components.LoadingContent
import com.nader.riyadalsalheen.ui.components.NavigationBreadcrumb
import com.nader.riyadalsalheen.ui.components.NavigationDrawer
import com.nader.riyadalsalheen.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

data class HadithDetailUiState(
    val books: List<Book> = emptyList(),
    val doors: List<Door> = emptyList(),
    val initHadithID: Int = 0,
    val hadithCount: Int = 0,
    val fontSize: Float = 18f,
    val bookmarks: List<Hadith> = emptyList(),
)

@Composable
fun HadithDetailScreen(
    viewModel: MainViewModel,
    onSearch: () -> Unit,
    onNavigateToHadith: (Int) -> Unit
) {
    val uiState = HadithDetailUiState(
        books = viewModel.books,
        doors = viewModel.doors,
        initHadithID = viewModel.currentHadithId,
        hadithCount = viewModel.hadithCount,
        fontSize = viewModel.fontSize.floatValue,
        bookmarks = viewModel.bookmarks.value
    )
    HadithDetailContent(
        uiState = uiState,
        getHadith = { viewModel.cachedHadiths[it] },
        getFirstHadithIdInDoor = { viewModel.getFirstHadithIdInDoor(it) },
        onSearch = onSearch,
        onNavigateToHadith = onNavigateToHadith,
        onLoadHadith = { viewModel.navigateToHadith(it) },
        onUpdateFontSize = { viewModel.updateFontSize(it) },
        onToggleBookmark = { viewModel.toggleBookmark(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithDetailContent(
    uiState : HadithDetailUiState,
    getHadith: (Int) -> HadithDetails?,
    getFirstHadithIdInDoor: (Int) -> Int?,
    onSearch: () -> Unit = {},
    onNavigateToHadith: (Int) -> Unit = {},
    onLoadHadith: (Int) -> Unit = {},
    onUpdateFontSize: (Float) -> Unit = {},
    onToggleBookmark: (Int) -> Unit = {}
)
{
    // Pager state for swipe navigation
    val pagerState = rememberPagerState(
        initialPage = uiState.initHadithID - 1,
        pageCount = { uiState.hadithCount }
    )
    val currentHadith = getHadith(pagerState.currentPage + 1)
    if(currentHadith == null)
    {
        return LoadingContent()
    }

    val isBookmarked = uiState.bookmarks.any { currentHadith.hadith.id == it.id }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showShareDialog by remember { mutableStateOf(false) }
    var showFontSizeDialog by remember { mutableStateOf(false) }
    val clipboard = LocalClipboard.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)


    // Track page changes
    LaunchedEffect(pagerState.currentPage) {
        onLoadHadith(pagerState.currentPage + 1)
    }

    // Update pager when hadith changes from other sources
    LaunchedEffect(uiState.initHadithID) {
        if (pagerState.currentPage != uiState.initHadithID - 1) {
            pagerState.scrollToPage(uiState.initHadithID - 1)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawer(
                books = uiState.books,
                doors = uiState.doors,
                hadithCount = uiState.hadithCount,
                currentBookId = currentHadith.book.id,
                currentDoorId = currentHadith.door.id,
                onNavigateToDoor = { doorId ->
                    coroutineScope.launch {
                        getFirstHadithIdInDoor(doorId)?.let {
                            onNavigateToHadith(it)
                        }
                    }
                },
                onClose = {
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_menu_24),
                                contentDescription = "القائمة"
                            )
                        }
                    },
                    title = {
                        Column {
                            Text(
                                text = "الحديث ${currentHadith.hadith.id}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = currentHadith.hadith.title,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                onToggleBookmark(currentHadith.hadith.id)
                                coroutineScope.launch {
                                    val message =
                                        if (isBookmarked) "تم إزالة الحديث من المفضلة" else "تم إضافة الحديث إلى المفضلة"
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(if (isBookmarked) R.drawable.ic_bookmark_filled_24 else R.drawable.ic_bookmark_24),
                                contentDescription = "المفضلة",
                                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { showFontSizeDialog = true }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_text_fields_24),
                                contentDescription = "حجم الخط"
                            )
                        }

                        IconButton(onClick = onSearch) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_search_24),
                                contentDescription = "البحث"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                beyondViewportPageCount = 1 // Pre-load adjacent pages
            ) { page ->
                // Show current hadith content or placeholder while loading
                HadithPageContent(
                    currentHadith = getHadith(page + 1),
                    fontSize = uiState.fontSize,
                    onShare = { showShareDialog = true }
                )
            }
        }
    }

    // Font Size Dialog
    if (showFontSizeDialog) {
        AlertDialog(
            onDismissRequest = { showFontSizeDialog = false },
            title = { Text("حجم الخط") },
            text = {
                Column {
                    Slider(
                        value = uiState.fontSize,
                        onValueChange = onUpdateFontSize,
                        valueRange = 14f..30f,
                        steps = 7
                    )
                    Text(
                        text = "الحجم: ${uiState.fontSize.toInt()}",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showFontSizeDialog = false }) {
                    Text("حسناً")
                }
            }
        )
    }

    // Share Dialog
    if (showShareDialog) {
        AlertDialog(
            onDismissRequest = { showShareDialog = false },
            title = { Text("مشاركة الحديث") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            val shareText = buildString {
                                appendLine(currentHadith.hadith.title)
                                appendLine()
                                appendLine(currentHadith.hadith.matn.replace(Regex("<[^>]*>"), ""))
                                appendLine()
                                appendLine("رياض الصالحين - الحديث ${currentHadith.hadith.id}")
                            }
                            showShareDialog = false

                            coroutineScope.launch {
                                val clipData = ClipData.newPlainText("hadith", shareText)
                                clipboard.setClipEntry(clipData.toClipEntry())
                                snackbarHostState.showSnackbar("تم نسخ الحديث")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("نسخ النص كاملاً")
                    }

                    TextButton(
                        onClick = {
                            val shareText = currentHadith.hadith.matn.replace(Regex("<[^>]*>"), "")
                            showShareDialog = false
                            coroutineScope.launch {
                                val clipData = ClipData.newPlainText("hadith", shareText)
                                clipboard.setClipEntry(clipData.toClipEntry())
                                snackbarHostState.showSnackbar("تم نسخ الحديث")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("نسخ نص الحديث فقط")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showShareDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun HadithPageContent(
    currentHadith: HadithDetails?,
    fontSize: Float,
    onShare: () -> Unit
) {
    if(currentHadith == null)
    {
        return LoadingContent()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Enhanced Navigation breadcrumb with sharp edges and long text support
        NavigationBreadcrumb(
            book = currentHadith.book,
            door = currentHadith.door
        )

        // Hadith Text
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_format_quote_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "نص الحديث",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onShare) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_share_24),
                            contentDescription = "مشاركة"
                        )
                    }
                }

                HtmlText(
                    html = currentHadith.hadith.matn,
                    fontSize = fontSize.sp,
                    lineHeight = (fontSize * 1.8f).sp
                )
            }
        }

        // Sharh (Explanation)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_menu_book_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "الشرح",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                HtmlText(
                    html = currentHadith.hadith.sharh,
                    fontSize = (fontSize - 2).sp,
                    lineHeight = (fontSize * 1.6f).sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}