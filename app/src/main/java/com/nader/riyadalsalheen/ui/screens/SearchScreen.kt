package com.nader.riyadalsalheen.ui.screens

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    onHadithSelected: (Int) -> Unit,
    onBackPressed: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    SearchScreenContent(
        searchResults = viewModel.searchResults.value,
        searchQuery = searchQuery,
        isSearching = viewModel.isSearching.value,
        onSearch = { query ->
            searchQuery = query
            viewModel.searchHadiths(query)
        },
        onHadithSelected = onHadithSelected,
        onBackPressed = onBackPressed,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenContent(
    searchResults: List<Hadith> = emptyList(),
    searchQuery: String = "",
    isSearching: Boolean = false,
    onSearch: (String) -> Unit = {},
    onHadithSelected: (Int) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { query ->
                            onSearch(if (query.length >= 3) query else "")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        placeholder = {
                            Text(
                                text = "ابحث عن كلمة أو جملة...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_search_24),
                                contentDescription = "بحث",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        onSearch("")
                                    }
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_clear_24),
                                        contentDescription = "مسح"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(28.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back_24),
                            contentDescription = "رجوع"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            // Search Info
            if (searchQuery.isNotEmpty() && searchQuery.length < 3) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_info_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "اكتب ٣ أحرف على الأقل للبحث",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            // Loading indicator
            if (isSearching) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Search Results
            when {
                // search edit box is empty
                searchQuery.isEmpty() -> {
                    // Initial state - show search tips
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_search_24),
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "ابحث في رياض الصالحين",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "يمكنك البحث في نص الحديث أو الشرح",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                // No results found
                searchResults.isEmpty() && searchQuery.length >= 3 && !isSearching -> {
                    // No results found
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_search_off_24),
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "لم يتم العثور على نتائج",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "جرب البحث بكلمات أخرى",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                // Show search results
                else -> {
                    Column {
                        if (searchResults.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                                )
                            ) {
                                Text(
                                    text = "عدد النتائج: ${searchResults.size}",
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(searchResults) { hadith ->
                                SearchResultItem(
                                    hadith = hadith,
                                    onClick = { onHadithSelected(hadith.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(
    hadith: Hadith,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الحديث ${hadith.id}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                if (hadith.title.isNotBlank()) {
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
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Highlight search query in text
            val cleanHadithText = hadith.matn.replace(Regex("<[^>]*>"), "")
            val displayText = if (cleanHadithText.length > 200) {
                cleanHadithText.substring(0, 200) + "..."
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
fun SearchScreenEmptyPreview() {
    RiyadalsalheenTheme {
        SearchScreenContent()
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
fun SearchScreenPreview() {
    RiyadalsalheenTheme {
        SearchScreenContent(
            searchResults = listOf(
                Hadith(
                    id = 1,
                    doorId = 1,
                    bookId = 1,
                    title = "إنما الأعمال بالنيات",
                    matn = "إنما الأعمال بالنيات وإنما لكل امرئ ما نوى",
                    sharh = "هذا الحديث يبين أن صحة العمل وفساده، وكونه مقبولاً أو مردوداً، إنما يتوقف على النية. فمن نوى خيراً أثيب، ومن نوى شراً عوقب، ومن لم ينو شيئاً فلا له ولا عليه."
                ),
                Hadith(
                    id = 2,
                    doorId = 2,
                    bookId = 1,
                    title = "الإسلام والإيمان والإحسان",
                    matn = "الإسلام أن تشهد أن لا إله إلا الله وأن محمداً رسول الله، وتقيم الصلاة، وتؤتي الزكاة، وتصوم رمضان، وتحج البيت إن استطعت إليه سبيلاً",
                    sharh = "هذا الحديث يعرّف الإسلام بأركانه الخمسة الأساسية، وهي الشهادتان والصلاة والزكاة والصوم والحج. وهذه الأركان هي الأسس التي يقوم عليها دين الإسلام."
                ),
                Hadith(
                    id = 3,
                    doorId = 3,
                    bookId = 2,
                    title = "طلب العلم",
                    matn = "اطلبوا العلم من المهد إلى اللحد",
                    sharh = "يحث هذا الحديث على أهمية طلب العلم في جميع مراحل الحياة، من الطفولة إلى الشيخوخة. فالعلم نور يهدي الإنسان في دنياه وآخرته، ولا يجوز التوقف عن طلبه في أي مرحلة من العمر."
                ),
                Hadith(
                    id = 4,
                    doorId = 4,
                    bookId = 2,
                    title = "بر الوالدين",
                    matn = "الوالدان أوسط أبواب الجنة، فإن شئت فأضع ذلك الباب أو احفظه",
                    sharh = "يبين هذا الحديث عظم مكانة الوالدين في الإسلام، وأن برهما طريق إلى الجنة. فمن أراد دخول الجنة فليبر والديه، ومن عقهما فقد أضاع فرصة عظيمة للفوز برضا الله."
                ),
                Hadith(
                    id = 5,
                    doorId = 5,
                    bookId = 3,
                    title = "الصدقة",
                    matn = "الصدقة تطفئ الخطيئة كما يطفئ الماء النار",
                    sharh = "يشبه هذا الحديث الصدقة بالماء الذي يطفئ النار، فكما أن الماء يقضي على النار، فإن الصدقة تمحو الذنوب والخطايا. وهذا يدل على عظم أجر الصدقة وأثرها في تطهير النفس."
                ),
                Hadith(
                    id = 6,
                    doorId = 6,
                    bookId = 3,
                    title = "الجار",
                    matn = "ما زال جبريل يوصيني بالجار حتى ظننت أنه سيورثه",
                    sharh = "يؤكد هذا الحديث على أهمية حسن الجوار وحقوق الجار في الإسلام. فقد كان الوصاة بالجار من الأمور المؤكدة حتى أن الرسول ظن أن الجار سيجعل له حق في الميراث."
                ),
                Hadith(
                    id = 7,
                    doorId = 7,
                    bookId = 4,
                    title = "الصبر",
                    matn = "الصبر نصف الإيمان، والوضوء نصف الإيمان، والحمد لله تملأ الميزان",
                    sharh = "يبين هذا الحديث أن الصبر يمثل جزءاً كبيراً من الإيمان، فالمؤمن الصابر على البلاء والمحن يكون إيمانه أقوى. كما يؤكد على أهمية الطهارة والحمد في حياة المسلم."
                ),
                Hadith(
                    id = 8,
                    doorId = 8,
                    bookId = 4,
                    title = "العفو والصفح",
                    matn = "ما نقصت صدقة من مال، وما زاد الله عبداً بعفو إلا عزاً",
                    sharh = "يوضح هذا الحديث أن العفو عن الناس لا يقلل من قدر الإنسان، بل يزيده عزة ومكانة عند الله وعند الناس. فالعفو خلق كريم يرفع صاحبه درجات."
                ),
                Hadith(
                    id = 9,
                    doorId = 9,
                    bookId = 5,
                    title = "الكلمة الطيبة",
                    matn = "الكلمة الطيبة صدقة",
                    sharh = "يعلمنا هذا الحديث أن مجرد النطق بالكلمة الحسنة الطيبة يعتبر صدقة يؤجر عليها المسلم. وهذا يشجع على حسن الكلام ولطف المعاملة مع الآخرين."
                ),
                Hadith(
                    id = 10,
                    doorId = 10,
                    bookId = 5,
                    title = "التواضع",
                    matn = "وما تواضع أحد لله إلا رفعه الله",
                    sharh = "يبين هذا الحديث أن التواضع لله تعالى سبب في رفعة الدرجات. فمن تواضع وترك الكبر والغرور، رفعه الله في الدنيا والآخرة، لأن التواضع من صفات المؤمنين الصالحين."
                )
            ),
            searchQuery = "السلام عليكم",
            isSearching = false,
        )
    }
}