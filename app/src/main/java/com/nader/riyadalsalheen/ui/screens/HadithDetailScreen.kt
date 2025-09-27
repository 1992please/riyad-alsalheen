package com.nader.riyadalsalheen.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
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
    val isDarkMode: Boolean
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
        bookmarks = viewModel.bookmarks.value,
        isDarkMode = viewModel.isDarkMode
    )
    HadithDetailContent(
        uiState = uiState,
        getHadith = { viewModel.cachedHadiths[it] },
        onSearch = onSearch,
        onNavigateToHadith = onNavigateToHadith,
        onLoadHadith = { viewModel.navigateToHadith(it) },
        onUpdateFontSize = { viewModel.updateFontSize(it) },
        onToggleBookmark = { viewModel.toggleBookmark(it) },
        onToggleDarkMode = {viewModel.toggleSystemTheme()},
        onShare = { viewModel.shareHadith(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithDetailContent(
    uiState : HadithDetailUiState,
    getHadith: (Int) -> HadithDetails?,
    onSearch: () -> Unit = {},
    onNavigateToHadith: (Int) -> Unit = {},
    onLoadHadith: (Int) -> Unit = {},
    onUpdateFontSize: (Float) -> Unit = {},
    onToggleBookmark: (Int) -> Unit = {},
    onToggleDarkMode: () -> Unit = {},
    onShare: (HadithDetails) -> Unit = {}
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
    var showFontSizeDialog by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var isToolbarVisible by remember { mutableStateOf(true) }

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
                bookmarks = uiState.bookmarks,
                hadithCount = uiState.hadithCount,
                isDarkMode = uiState.isDarkMode,
                onNavigateToHadith = onNavigateToHadith,
                onFontSizeChange = { showFontSizeDialog = true },
                onClose = { coroutineScope.launch { drawerState.close() } },
                onToggleDarkMode = onToggleDarkMode
            )
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                AnimatedVisibility(
                    visible = isToolbarVisible,
                    enter = slideInVertically(initialOffsetY = { -it }),
                    exit = slideOutVertically(targetOffsetY = { -it })
                ) {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.ic_menu_24),
                                    contentDescription = "القائمة"
                                )
                            }
                        },
                        title = {},
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
            }
        ) { paddingValues ->

            val animatedPadding by animateDpAsState(
                targetValue = if (isToolbarVisible) paddingValues.calculateTopPadding() else 0.dp,
                label = "contentPaddingAnim"
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = animatedPadding,
                        start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                        end = paddingValues.calculateEndPadding(LocalLayoutDirection.current),
                        bottom = 0.dp
                    ),
                beyondViewportPageCount = 1 // Pre-load adjacent pages
            ) { page ->
                // Show current hadith content or placeholder while loading
                HadithPageContent(
                    currentHadith = getHadith(page + 1),
                    fontSize = uiState.fontSize,
                    onTap = {isToolbarVisible = !isToolbarVisible},
                    onLongTap = { onShare(currentHadith) }
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
}

@Composable
fun HadithPageContent(
    currentHadith: HadithDetails?,
    fontSize: Float,
    onTap: () -> Unit,
    onLongTap: () -> Unit
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
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // No ripple for the background tap
                onClick = onTap
            )
    ) {

        NavigationBreadcrumb(
            book = currentHadith.book,
            door = currentHadith.door
        )

        // Hadith Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = currentHadith.hadith.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "الحديث رقم ${currentHadith.hadith.id}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .combinedClickable(
                    onClick = onTap,
                    onLongClick = onLongTap
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                HtmlText(
                    html = currentHadith.hadith.matn,
                    fontSize = fontSize.sp,
                    lineHeight = (fontSize * 1.8f).sp,
                    //style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                HtmlText(
                    html = currentHadith.hadith.sharh,
                    fontSize = (fontSize - 2).sp,
                    lineHeight = (fontSize * 1.6f).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}