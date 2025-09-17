package com.nader.riyadalsalheen.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nader.riyadalsalheen.R
import com.nader.riyadalsalheen.model.Hadith
import com.nader.riyadalsalheen.ui.theme.RiyadalsalheenTheme
import com.nader.riyadalsalheen.ui.viewmodel.MainViewModel

@Composable
fun BookmarksScreen(
    viewModel: MainViewModel,
    onHadithSelected: (Int) -> Unit,
    onBackPressed: () -> Unit
)
{
    BookmarksScreenContent(
        bookmarkedHadiths = viewModel.bookmarks.value,
        onHadithSelected = onHadithSelected,
        onRemoveBookmark = { hadithId ->
            viewModel.toggleBookmark(hadithId = hadithId)
        },
        onBackPressed = onBackPressed
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreenContent(
    bookmarkedHadiths: List<Hadith>,
    onHadithSelected: (Int) -> Unit = {},
    onRemoveBookmark: (Int) -> Unit = {},
    onBackPressed: () -> Unit = {}
)
{
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("المفضلة")
                        if (bookmarkedHadiths.isNotEmpty()) {
                            Text(
                                text = "${bookmarkedHadiths.size} حديث",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "رجوع"
                        )
                    }
                },
                actions = {
                    if (bookmarkedHadiths.isNotEmpty()) {
                        var showClearDialog by remember { mutableStateOf(false) }

                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_delete_sweep_24),
                                contentDescription = "مسح الكل"
                            )
                        }

                        if (showClearDialog) {
                            AlertDialog(
                                onDismissRequest = { showClearDialog = false },
                                title = { Text("مسح جميع المفضلات") },
                                text = { Text("هل أنت متأكد من مسح جميع الأحاديث المفضلة؟") },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            bookmarkedHadiths.forEach { hadith ->
                                                onRemoveBookmark(hadith.id)
                                            }
                                            showClearDialog = false
                                        }
                                    ) {
                                        Text("مسح", color = MaterialTheme.colorScheme.error)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showClearDialog = false }) {
                                        Text("إلغاء")
                                    }
                                }
                            )
                        }
                    }
                }
            )
        }
    )
    { paddingValues ->
        if (bookmarkedHadiths.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_bookmark_border_24),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "لا توجد أحاديث مفضلة",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "اضغط على أيقونة المفضلة في صفحة الحديث لإضافته هنا",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = bookmarkedHadiths.sortedBy { it.id },
                    key = { it.id }
                ) { hadith ->
                    BookmarkItem(
                        hadith = hadith,
                        onItemClick = { onHadithSelected(hadith.id) },
                        onRemoveBookmark = { onRemoveBookmark(hadith.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun BookmarkItem(
    hadith: Hadith,
    onItemClick: () -> Unit,
    onRemoveBookmark: () -> Unit
) {
    var showRemoveDialog by remember { mutableStateOf(false) }

    Card(
        onClick = onItemClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "الحديث ${hadith.id}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = hadith.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val cleanHadithText = hadith.hadith.replace(Regex("<[^>]*>"), "")
                val displayText = if (cleanHadithText.length > 150) {
                    cleanHadithText.substring(0, 150) + "..."
                } else {
                    cleanHadithText
                }

                Text(
                    text = displayText,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { showRemoveDialog = true },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_bookmark_remove_24),
                    contentDescription = "إزالة من المفضلة",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }

    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = { Text("إزالة من المفضلة") },
            text = { Text("هل تريد إزالة هذا الحديث من المفضلة؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemoveBookmark()
                        showRemoveDialog = false
                    }
                ) {
                    Text("إزالة", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
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
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    locale = "ar"
)
@Composable
fun BookmarksContentEmptyPreview() {
    RiyadalsalheenTheme {
        BookmarksScreenContent(
            bookmarkedHadiths = emptyList()
        )
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
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    locale = "ar"
)
@Composable
fun BookmarksContentPreview() {
    RiyadalsalheenTheme {
        BookmarksScreenContent(
            bookmarkedHadiths = listOf(
                Hadith(
                    id = 1,
                    doorId = 1,
                    bookId = 1,
                    title = "إنما الأعمال بالنيات",
                    hadith = "إنما الأعمال بالنيات وإنما لكل امرئ ما نوى",
                    sharh = "هذا الحديث يبين أن صحة العمل وفساده، وكونه مقبولاً أو مردوداً، إنما يتوقف على النية. فمن نوى خيراً أثيب، ومن نوى شراً عوقب، ومن لم ينو شيئاً فلا له ولا عليه."
                ),
                Hadith(
                    id = 2,
                    doorId = 2,
                    bookId = 1,
                    title = "الإسلام والإيمان والإحسان",
                    hadith = "الإسلام أن تشهد أن لا إله إلا الله وأن محمداً رسول الله، وتقيم الصلاة، وتؤتي الزكاة، وتصوم رمضان، وتحج البيت إن استطعت إليه سبيلاً",
                    sharh = "هذا الحديث يعرّف الإسلام بأركانه الخمسة الأساسية، وهي الشهادتان والصلاة والزكاة والصوم والحج. وهذه الأركان هي الأسس التي يقوم عليها دين الإسلام."
                ),
                Hadith(
                    id = 3,
                    doorId = 3,
                    bookId = 2,
                    title = "طلب العلم",
                    hadith = "اطلبوا العلم من المهد إلى اللحد",
                    sharh = "يحث هذا الحديث على أهمية طلب العلم في جميع مراحل الحياة، من الطفولة إلى الشيخوخة. فالعلم نور يهدي الإنسان في دنياه وآخرته، ولا يجوز التوقف عن طلبه في أي مرحلة من العمر."
                ),
                Hadith(
                    id = 4,
                    doorId = 4,
                    bookId = 2,
                    title = "بر الوالدين",
                    hadith = "الوالدان أوسط أبواب الجنة، فإن شئت فأضع ذلك الباب أو احفظه",
                    sharh = "يبين هذا الحديث عظم مكانة الوالدين في الإسلام، وأن برهما طريق إلى الجنة. فمن أراد دخول الجنة فليبر والديه، ومن عقهما فقد أضاع فرصة عظيمة للفوز برضا الله."
                ),
                Hadith(
                    id = 5,
                    doorId = 5,
                    bookId = 3,
                    title = "الصدقة",
                    hadith = "الصدقة تطفئ الخطيئة كما يطفئ الماء النار",
                    sharh = "يشبه هذا الحديث الصدقة بالماء الذي يطفئ النار، فكما أن الماء يقضي على النار، فإن الصدقة تمحو الذنوب والخطايا. وهذا يدل على عظم أجر الصدقة وأثرها في تطهير النفس."
                ),
                Hadith(
                    id = 6,
                    doorId = 6,
                    bookId = 3,
                    title = "الجار",
                    hadith = "ما زال جبريل يوصيني بالجار حتى ظننت أنه سيورثه",
                    sharh = "يؤكد هذا الحديث على أهمية حسن الجوار وحقوق الجار في الإسلام. فقد كان الوصاة بالجار من الأمور المؤكدة حتى أن الرسول ظن أن الجار سيجعل له حق في الميراث."
                ),
                Hadith(
                    id = 7,
                    doorId = 7,
                    bookId = 4,
                    title = "الصبر",
                    hadith = "الصبر نصف الإيمان، والوضوء نصف الإيمان، والحمد لله تملأ الميزان",
                    sharh = "يبين هذا الحديث أن الصبر يمثل جزءاً كبيراً من الإيمان، فالمؤمن الصابر على البلاء والمحن يكون إيمانه أقوى. كما يؤكد على أهمية الطهارة والحمد في حياة المسلم."
                ),
                Hadith(
                    id = 8,
                    doorId = 8,
                    bookId = 4,
                    title = "العفو والصفح",
                    hadith = "ما نقصت صدقة من مال، وما زاد الله عبداً بعفو إلا عزاً",
                    sharh = "يوضح هذا الحديث أن العفو عن الناس لا يقلل من قدر الإنسان، بل يزيده عزة ومكانة عند الله وعند الناس. فالعفو خلق كريم يرفع صاحبه درجات."
                ),
                Hadith(
                    id = 9,
                    doorId = 9,
                    bookId = 5,
                    title = "الكلمة الطيبة",
                    hadith = "الكلمة الطيبة صدقة",
                    sharh = "يعلمنا هذا الحديث أن مجرد النطق بالكلمة الحسنة الطيبة يعتبر صدقة يؤجر عليها المسلم. وهذا يشجع على حسن الكلام ولطف المعاملة مع الآخرين."
                ),
                Hadith(
                    id = 10,
                    doorId = 10,
                    bookId = 5,
                    title = "التواضع",
                    hadith = "وما تواضع أحد لله إلا رفعه الله",
                    sharh = "يبين هذا الحديث أن التواضع لله تعالى سبب في رفعة الدرجات. فمن تواضع وترك الكبر والغرور، رفعه الله في الدنيا والآخرة، لأن التواضع من صفات المؤمنين الصالحين."
                )
            )
        )
    }
}