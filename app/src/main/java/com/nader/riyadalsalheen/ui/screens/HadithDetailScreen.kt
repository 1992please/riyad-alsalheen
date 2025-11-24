package com.nader.riyadalsalheen.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Velocity
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
import com.nader.riyadalsalheen.ui.components.TextType
import com.nader.riyadalsalheen.ui.viewmodel.MainViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface ScrollEvent {
    data class Drag(val delta: Float) : ScrollEvent
    data class Fling(val velocity: Float) : ScrollEvent
}

fun shareHadith(context: Context, hadithDetails: HadithDetails) {
    val shareText = hadithDetails.hadith.matn.replace(Regex("<[^>]*>"), "")

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, hadithDetails.hadith.title)
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    // Use the Android Sharesheet
    context.startActivity(Intent.createChooser(intent, "Share Hadith"))
}

data class HadithDetailUiState(
    val books: List<Book> = emptyList(),
    val doors: List<Door> = emptyList(),
    val initHadithID: Int = 0,
    val hadithCount: Int = 0,
    val fontSize: Float = 18f,
    val bookmarks: List<Hadith> = emptyList()
)

@Composable
fun HadithDetailScreen(
    initHadithID: Int,
    viewModel: MainViewModel,
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
        onOpenDrawer = onOpenDrawer,
        loadAndGetHadith = { viewModel.loadAndGetHadith(it) },
        onToggleBookmark = { viewModel.toggleBookmark(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithDetailContent(
    uiState : HadithDetailUiState,
    getHadith: (Int) -> HadithDetails?,
    loadAndGetHadith: (Int) -> HadithDetails?,
    onOpenDrawer: () -> Unit = {},
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
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // FullScreen
    var isFullScreen by remember { mutableStateOf(false) }
    if (isFullScreen) {
        HideSystemBars()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AnimatedVisibility(
                visible = !isFullScreen,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it })
            ) {
                TopAppBarContent(
                    currentHadith = currentHadith,
                    isBookmarked = isBookmarked,
                    onMenuClick = onOpenDrawer,
                    onBookmarkClick = {
                        onToggleBookmark(currentHadith.hadith.id)
                        coroutineScope.launch {
                            val message =
                                if (isBookmarked) "تم إزالة الحديث من المفضلة" else "تم إضافة الحديث إلى المفضلة"
                            snackbarHostState.showSnackbar(message)
                        }
                    },
                    onShareClick = { shareHadith(context, currentHadith) }
                )
            }
        }
    ) { paddingValues ->
        val animatedPadding by animateDpAsState(
            targetValue = if (isFullScreen) 0.dp else paddingValues.calculateTopPadding(),
            label = "contentPaddingAnim"
        )

        val verticalScrollChannel = remember { Channel<ScrollEvent>(Channel.UNLIMITED) }

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(
                top = animatedPadding,
                start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                end = paddingValues.calculateEndPadding(LocalLayoutDirection.current),
                bottom = 0.dp
            )) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize(),
                beyondViewportPageCount = 1
            ) { page ->
                // Show current hadith content or placeholder while loading
                HadithPageContent(
                    currentHadith = getHadith(page + 1),
                    fontSize = uiState.fontSize,
                    scrollChannel = verticalScrollChannel,
                    isCurrentPage = page == pagerState.currentPage
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = null,
                        indication = null, // No ripple for the background tap
                        onClick = { isFullScreen = !isFullScreen }
                    )
                    .pointerInput(Unit) {
                        var velocity = Velocity.Zero
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume() // We consume the touch
                                val deltaTime =
                                    (change.uptimeMillis - change.previousUptimeMillis).coerceAtLeast(
                                        1L
                                    )
                                val drag = dragAmount * 1000f / deltaTime.toFloat()
                                velocity = Velocity(drag.x, drag.y)
                                val (dx, dy) = dragAmount

                                // Determine intention (Locking logic)
                                val isHorizontalDrag = kotlin.math.abs(dx) > kotlin.math.abs(dy)

                                if (isHorizontalDrag) {
                                    pagerState.dispatchRawDelta(dx)
//                                    coroutineScope.launch {
//                                        pagerState.scrollBy(dx)
//                                    }
                                } else {
                                    verticalScrollChannel.trySend(ScrollEvent.Drag(-dy))
                                }
                            },
                            onDragEnd = {
                                val isHorizontalSwipe =
                                    kotlin.math.abs(velocity.x) > kotlin.math.abs(velocity.y)
                                var targetPage = pagerState.currentPage
                                if (isHorizontalSwipe) {
                                    targetPage = when {
                                        pagerState.currentPageOffsetFraction > 0.5f -> pagerState.currentPage + 1
                                        pagerState.currentPageOffsetFraction < -0.5f -> pagerState.currentPage - 1
                                        velocity.x > 2000 && pagerState.currentPageOffsetFraction > 0.1f -> pagerState.currentPage + 1  // Fast swipe right
                                        velocity.x < -2000 && pagerState.currentPageOffsetFraction < -0.1f -> pagerState.currentPage - 1 // Fast swipe left
                                        else -> pagerState.currentPage
                                    }
                                } else {
                                    verticalScrollChannel.trySend(ScrollEvent.Fling(-velocity.y))
                                }

                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(
                                        targetPage.coerceIn(
                                            0,
                                            pagerState.pageCount - 1
                                        )
                                    )
                                }
                            },
                            onDragCancel = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage)
                                }
                            }
                        )
                    }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarContent(
    currentHadith: HadithDetails,
    isBookmarked: Boolean,
    onMenuClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onShareClick: () -> Unit
) {
    TopAppBar(
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
            ) {
                Text(
                    text = currentHadith.door.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = currentHadith.book.title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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

            IconButton(onClick = onShareClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_share_24),
                    contentDescription = "Share",
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

@Composable
fun HadithPageContent(
    currentHadith: HadithDetails?,
    fontSize: Float,
    scrollChannel: Channel<ScrollEvent>, // Receive the channel
    isCurrentPage: Boolean // Know if I am the active page
) {
    if(currentHadith == null) {
        return LoadingContent()
    }

    val scrollState = rememberScrollState()

    LaunchedEffect(isCurrentPage) {
        if (isCurrentPage) {
            // Only the visible page listens to the scroll commands
            scrollChannel.receiveAsFlow().collect { event ->
                when(event){
                    is ScrollEvent.Drag -> {
                        scrollState.scrollBy(event.delta)
                    }
                    is ScrollEvent.Fling->{
                        // Calculate distance based on velocity and decay
                        val decay = exponentialDecay<Float>()
                        val targetValue = decay.calculateTargetValue(0f, event.velocity * .5f)

                        scrollState.animateScrollBy(
                            value = targetValue,
                            animationSpec = tween(
                                durationMillis = 400,
                                easing = LinearOutSlowInEasing
                            )
                        )
                    }
                }

            }
        }
    }

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
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                HtmlText(
                    htmlText = currentHadith.hadith.matn,
                    textType = TextType.HADITH,
                    fontSize = fontSize.sp,
                    lineHeight = (fontSize * 1.8f).sp,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                HtmlText(
                    htmlText = currentHadith.hadith.sharh,
                    textType = TextType.SHARH,
                    fontSize = fontSize.sp,
                    lineHeight = (fontSize * 1.6f).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}