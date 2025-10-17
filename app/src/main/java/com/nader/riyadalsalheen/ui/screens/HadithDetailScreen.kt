package com.nader.riyadalsalheen.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nader.riyadalsalheen.R
import com.nader.riyadalsalheen.model.Book
import com.nader.riyadalsalheen.model.Door
import com.nader.riyadalsalheen.model.Hadith
import com.nader.riyadalsalheen.model.HadithDetails
import com.nader.riyadalsalheen.ui.components.HideSystemBars
import com.nader.riyadalsalheen.ui.components.HtmlText
import com.nader.riyadalsalheen.ui.components.LoadingContent
import com.nader.riyadalsalheen.ui.components.NavigationBottomSheet
import com.nader.riyadalsalheen.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch


data class HadithDetailUiState(
    val books: List<Book> = emptyList(),
    val doors: List<Door> = emptyList(),
    val initHadithID: Int = 0,
    val hadithCount: Int = 0,
    val fontSize: Float = 18f,
    val bookmarks: List<Hadith> = emptyList()
)

fun shareHadith(context: Context, hadithDetails: HadithDetails) {
    val shareText = buildString {
        appendLine("📖 ${hadithDetails.hadith.title}")
        appendLine()
        appendLine(hadithDetails.hadith.matn.replace(Regex("<[^>]*>"), "")) // Clean HTML tags for sharing
        appendLine()
        appendLine("Source: Riyad al Saleheen - Hadith #${hadithDetails.hadith.id}")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, hadithDetails.hadith.title)
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    // Use the Android Sharesheet
    context.startActivity(Intent.createChooser(intent, "Share Hadith"))
}

@Composable
fun HadithDetailScreen(
    initHadithID: Int,
    viewModel: MainViewModel,
    onLoadDoor: (Int) -> Unit,
    onSearch: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    val uiState = HadithDetailUiState(
        books = viewModel.books,
        doors = viewModel.doors,
        initHadithID = initHadithID,
        hadithCount = viewModel.hadithCount,
        fontSize = viewModel.fontSize.floatValue,
        bookmarks = viewModel.bookmarks.value
    )

    HadithDetailContent(
        uiState = uiState,
        getHadith = { viewModel.cachedHadiths[it] },
        onSearch = onSearch,
        onOpenDrawer = onOpenDrawer,
        loadAndGetHadith = { viewModel.loadAndGetHadith(it) },
        onLoadDoor = onLoadDoor,
        onToggleBookmark = { viewModel.toggleBookmark(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithDetailContent(
    uiState : HadithDetailUiState,
    getHadith: (Int) -> HadithDetails?,
    loadAndGetHadith: (Int) -> HadithDetails?,
    onSearch: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    onLoadDoor: (Int) -> Unit = {},
    onToggleBookmark: (Int) -> Unit = {}
) {
    // Pager state for swipe navigation
    val pagerState = rememberPagerState(
        initialPage = uiState.initHadithID - 1,
        pageCount = { uiState.hadithCount }
    )

    val currentHadithId = pagerState.currentPage + 1
    val currentHadith = loadAndGetHadith(currentHadithId)

    if (currentHadith == null) {
        return LoadingContent()
    }
    val context = LocalContext.current

    val isBookmarked = uiState.bookmarks.any { currentHadith.hadith.id == it.id }
    var showBottomSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // FullScreen
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val isFullScreen =  scrollBehavior.state.collapsedFraction != 0f
    if (isFullScreen) {
        HideSystemBars()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBarContent(
                currentHadith = currentHadith,
                isBookmarked = isBookmarked,
                scrollBehavior = scrollBehavior,
                onMenuClick = onOpenDrawer,
                onTitleClick = { showBottomSheet = true },
                onBookmarkClick = {
                    onToggleBookmark(currentHadith.hadith.id)
                    coroutineScope.launch {
                        val message =
                            if (isBookmarked) "تم إزالة الحديث من المفضلة" else "تم إضافة الحديث إلى المفضلة"
                        snackbarHostState.showSnackbar(message)
                    }
                },
                onSearchClick = onSearch
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
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
                onLongTap = { shareHadith(context, currentHadith) }
            )
        }
    }

    if (showBottomSheet) {
        NavigationBottomSheet(
            books = uiState.books,
            doors = uiState.doors,
            currentBookId = currentHadith.book.id,
            currentDoorId = currentHadith.door.id,
            onNavigateToDoor = { doorId ->
                onLoadDoor(doorId)
                showBottomSheet = false
            },
            onDismiss = {
                showBottomSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarContent(
    currentHadith: HadithDetails,
    isBookmarked: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
    onMenuClick: () -> Unit,
    onTitleClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Column {
        TopAppBar(
            expandedHeight = 80.dp,
            scrollBehavior = scrollBehavior,
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_menu_24),
                        contentDescription = "القائمة",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            title = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onTitleClick)
                ) {
                    Text(
                        text = currentHadith.door.title,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Text(
                        text = currentHadith.book.title,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            },
            actions = {
                IconButton(onClick = onBookmarkClick) {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            if (isBookmarked) R.drawable.ic_bookmark_filled_24
                            else R.drawable.ic_bookmark_24
                        ),
                        contentDescription = "المفضلة",
                        tint = if (isBookmarked)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_search_24),
                        contentDescription = "البحث",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
            )
        )
    }
}

@Composable
fun HadithPageContent(
    currentHadith: HadithDetails?,
    fontSize: Float,
    onLongTap: () -> Unit
) {
    if(currentHadith == null) {
        return LoadingContent()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        // Hadith Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "الحديث رقم ${currentHadith.hadith.id}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
            Text(
                text = currentHadith.hadith.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )

        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { },
                    onLongClick = onLongTap
                ),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                HtmlText(
                    html = currentHadith.hadith.matn,
                    fontSize = fontSize.sp,
                    lineHeight = (fontSize * 1.8f).sp,
                    style = MaterialTheme.typography.bodyLarge
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