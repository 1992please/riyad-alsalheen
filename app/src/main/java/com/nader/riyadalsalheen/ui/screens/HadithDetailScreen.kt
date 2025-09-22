package com.nader.riyadalsalheen.ui.screens

import android.content.ClipData
import android.content.res.Configuration
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nader.riyadalsalheen.R
import com.nader.riyadalsalheen.model.Book
import com.nader.riyadalsalheen.model.Door
import com.nader.riyadalsalheen.model.Hadith
import com.nader.riyadalsalheen.ui.components.HtmlText
import com.nader.riyadalsalheen.ui.components.LoadingContent
import com.nader.riyadalsalheen.ui.components.NavigationBreadcrumb
import com.nader.riyadalsalheen.ui.theme.RiyadalsalheenTheme
import com.nader.riyadalsalheen.ui.viewmodel.HadithDetails
import com.nader.riyadalsalheen.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

data class HadithDetailUiState(
    val hadithCount: Int = 0,
    val fontSize: Float = 18f,
    val bookmarks: List<Hadith> = emptyList(),
)

@Composable
fun HadithDetailScreen(
    viewModel: MainViewModel,
    onSearch: () -> Unit
) {
    val currentHadith = viewModel.currentHadith.value
    if (currentHadith == null) {
        return LoadingContent()
    }

    val uiState = HadithDetailUiState(
        hadithCount = viewModel.hadithCount.intValue,
        fontSize = viewModel.fontSize.floatValue,
        bookmarks = viewModel.bookmarks.value
    )
    HadithDetailContent(
        uiState = uiState,
        getHadith = { id -> viewModel.cachedHadiths[id] ?:currentHadith },
        onSearch = onSearch,
        onLoadHadith = { id ->
            viewModel.navigateToHadith(id)
        },
        onUpdateFontSize = { size ->
            viewModel.updateFontSize(size)
        },
        onToggleBookmark = {
            viewModel.toggleBookmark(currentHadith.hadith.id)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithDetailContent(
    uiState : HadithDetailUiState,
    getHadith: (Int) -> HadithDetails,
    onSearch: () -> Unit = {},
    onLoadHadith: (Int) -> Unit = {},
    onUpdateFontSize: (Float) -> Unit = {},
    onToggleBookmark: () -> Unit = {}
)
{
    val currentHadith = getHadith(0)
    val isBookmarked = uiState.bookmarks.any { currentHadith.hadith.id == it.id }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showShareDialog by remember { mutableStateOf(false) }
    var showFontSizeDialog by remember { mutableStateOf(false) }
    val clipboard = LocalClipboard.current

    // Pager state for swipe navigation
    val pagerState = rememberPagerState(
        initialPage = currentHadith.hadith.id - 1,
        pageCount = { uiState.hadithCount }
    )

    // Track page changes
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage + 1 != currentHadith.hadith.id) {
            onLoadHadith(pagerState.currentPage + 1)
        }
    }

    // Update pager when hadith changes from other sources
    LaunchedEffect(currentHadith.hadith.id) {
        if (pagerState.currentPage != currentHadith.hadith.id - 1) {
            pagerState.animateScrollToPage(currentHadith.hadith.id - 1)
        }
    }

    Scaffold (
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onSearch) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_search_24),
                            contentDescription = "البحث"
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
                            onToggleBookmark()
                            coroutineScope.launch {
                                val message = if (isBookmarked) "تم إزالة الحديث من المفضلة" else "تم إضافة الحديث إلى المفضلة"
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ){ paddingValues ->
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
    currentHadith: HadithDetails,
    fontSize: Float,
    onShare: () -> Unit
) {
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

@Preview(
    name = "Default Light Theme",
    showBackground = true,
    locale = "ar"
)
@Preview(
    name = "Dark Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    locale = "ar"
)
@Composable
fun HadithDetailPreview() {
    RiyadalsalheenTheme {
        HadithDetailContent(
            uiState = HadithDetailUiState(
                hadithCount = 10,
                fontSize = 18f
            ),
            getHadith = {
                HadithDetails(
                    hadith = Hadith(
                        id = 3,
                        doorId = 4,
                        bookId = 8,
                        title = "إنما الأعمال بالنيات",
                        matn = "<p dir=\"rtl\" style=\"text-align:justify\"><span style=\"font-size:18pt\"><span style=\"font-family:tahoma,geneva,sans-serif\"><span style=\"background-color:transparent; color:rgb(0, 0, 0)\">وعَنْ أَميرِ الْمُؤْمِنِينَ أبي حفْصٍ عُمرَ بنِ الْخَطَّابِ بْن نُفَيْل بْنِ عَبْد الْعُزَّى بن رياح بْن عبدِ اللَّهِ بْن قُرْطِ بْنِ رزاح بْنِ عَدِيِّ بْن كَعْبِ بْن لُؤَيِّ بن غالبٍ القُرَشِيِّ العدويِّ . رضي الله عنه ، قال : سمعْتُ رسُولَ الله صَلّى اللهُ عَلَيْهِ وسَلَّم يقُولُ &nbsp;</span><span style=\"background-color:transparent; color:rgb(0, 0, 255)\">&laquo; </span><span style=\"background-color:transparent; color:rgb(0, 0, 255)\">إنَّما</span><span style=\"background-color:transparent; color:rgb(0, 0, 255)\"> الأَعمالُ </span><span style=\"background-color:transparent; color:rgb(0, 0, 255)\">بالنِّيَّات</span><span style=\"background-color:transparent; color:rgb(0, 0, 255)\"> ، وإِنَّمَا لِكُلِّ امرئٍ مَا نَوَى ، فمنْ كانَتْ </span><span style=\"background-color:transparent; color:rgb(0, 0, 255)\">هجْرَتُهُ</span><span style=\"background-color:transparent; color:rgb(0, 0, 255)\"> إِلَى الله ورَسُولِهِ فهجرتُه إلى الله ورسُولِهِ ، ومنْ كاَنْت هجْرَتُه لدُنْيَا يُصيبُها ، أَو امرَأَةٍ يَنْكحُها فهْجْرَتُهُ إلى ما هَاجَر إليْهِ &raquo;</span></span></span></p>\n" +
                                "\n" +
                                "\n" +
                                "\n" +
                                "<p dir=\"rtl\" style=\"text-align:justify\"><span style=\"font-size:18pt\"><span style=\"font-family:tahoma,geneva,sans-serif\"><span style=\"background-color:transparent; color:rgb(0, 0, 0)\">متَّفَقٌ على صحَّتِه. رواهُ إِماما المُحَدِّثِين: أَبُو عَبْدِ الله مُحَمَّدُ بنُ إِسْمَاعيل بْن إِبْراهيمَ بْن الْمُغيرة بْن برْدزْبَهْ الْجُعْفِيُّ &nbsp;الْبُخَارِيُّ، وَأَبُو الحُسَيْنِ مُسْلمُ بْن الْحَجَّاجِ بن مُسلمٍ القُشَيْريُّ &nbsp;النَّيْسَابُوريُّ رَضَيَ الله عَنْهُمَا في صَحيحيهِما اللَّذَيْنِ هما أَصَحُّ الْكُتُبِ الْمُصَنَّفَة .</span></span></span></p>\n" +
                                "\n" +
                                "\n" +
                                "\n" +
                                "<div>&nbsp;</div>\n" +
                                "\n" +
                                "\n" +
                                "\n" +
                                "<p>&nbsp;</p>",
                        sharh = "<p dir=\"rtl\" style=\"text-align:justify\"><span style=\"font-size:18pt\"><span style=\"font-family:tahoma,geneva,sans-serif\"><span style=\"background-color:transparent; color:rgb(255, 0, 0)\">لغة الحديث</span><span style=\"background-color:transparent; color:rgb(152, 0, 0)\"> :</span></span></span></p>\n" +
                                "\n" +
                                "\n" +
                                "\n" +
                                "<p dir=\"rtl\" style=\"text-align:justify\"><span style=\"font-size:18pt\"><span style=\"font-family:tahoma,geneva,sans-serif\"><span style=\"background-color:transparent; color:rgb(152, 0, 0)\">الحفص :</span><span style=\"background-color:transparent; color:rgb(0, 0, 0)\"> الأسد وأبو حفص كنية لعمر بن الخطاب .</span><br />\n" +
                                "\n" +
                                "<span style=\"background-color:transparent; color:rgb(152, 0, 0)\">إنما</span><span style=\"background-color:transparent; color:rgb(0, 0, 0)\"> : أداة حصر تفيد تقوية الحكم المذكور بعدها .</span><br />\n" +
                                "\n" +
                                "<span style=\"background-color:transparent; color:rgb(152, 0, 0)\">النيات</span><span style=\"background-color:transparent; color:rgb(0, 0, 0)\"> : جمع نية ، وهي مصدر أو اسم مصدر ، وهي في اللغة : القصد ، وفي الشرع : قصد الشيء مقترناً بفعله .</span><br />\n" +
                                "\n" +
                                "<span style=\"background-color:transparent; color:rgb(152, 0, 0)\">الهجرة</span><span style=\"background-color:transparent; color:rgb(0, 0, 0)\"> : لغة : الترك ، وشرعاً : مفارقة دار الكفر إلى دار الإسلام خوف الفتنة . </span></span></span></p>\n" +
                                "\n" +
                                "\n" +
                                "\n" +
                                "<p dir=\"rtl\" style=\"text-align:justify\"><span style=\"font-size:18pt\"><span style=\"font-family:tahoma,geneva,sans-serif\"><span style=\"background-color:transparent; color:rgb(255, 0, 0)\">أفاد الحديث</span><span style=\"background-color:transparent; color:rgb(0, 0, 0)\"> : </span></span></span></p>\n" +
                                "\n" +
                                "\n" +
                                "\n" +
                                "<ol>\n" +
                                "\n" +
                                "\t<li dir=\"rtl\">\n" +
                                "\n" +
                                "\t<p dir=\"rtl\" style=\"text-align:justify\"><span style=\"font-size:18pt\"><span style=\"font-family:tahoma,geneva,sans-serif\"><span style=\"background-color:transparent\">اتفق العلماء على أن النية في الأعمال لا بد منها ليترتب الثواب على فعلها ، ولكنهم فصَّلوا في جعلها شرطاً لصحة الأعمال ، فالشافعية قالوا : إنها شرط في الوسائل كالوضوء ، والمقاصد كالصلاة ، وقالت الحنفية : إن النية شرط في المقاصد لا في الوسائل</span></span></span></p>\n" +
                                "\n" +
                                "\t</li>\n" +
                                "\n" +
                                "\t<li dir=\"rtl\">\n" +
                                "\n" +
                                "\t<p dir=\"rtl\" style=\"text-align:justify\"><span style=\"font-size:18pt\"><span style=\"font-family:tahoma,geneva,sans-serif\"><span style=\"background-color:transparent\">محل النية القلب ولا يُشرع التلفظ بها &nbsp;</span></span></span></p>\n" +
                                "\n" +
                                "\t</li>\n" +
                                "\n" +
                                "\t<li dir=\"rtl\">\n" +
                                "\n" +
                                "\t<p dir=\"rtl\" style=\"text-align:justify\"><span style=\"font-size:18pt\"><span style=\"font-family:tahoma,geneva,sans-serif\"><span style=\"background-color:transparent\">الإخلاص لله تعالى في العمل شرط من شروط قبوله ، فإن الله تعالى لا يقبل من العمل إلا ما كان خالصاً لوجهه الكريم .</span></span></span></p>\n" +
                                "\n" +
                                "\t</li>\n" +
                                "\n" +
                                "</ol>"
                    ),
                    door= Door(2, 3, "باب تعظيم حرمات المسلمين وبيان حقوقهم والشفقة عليهم ورحمتهم"),
                    book= Book(3, "كتاب عيادة المريض وتشييع الميت والصلاة عليه وحضور دفنه والمكث عند قبره بعد دفنه")
                )
            }
        )
    }
}