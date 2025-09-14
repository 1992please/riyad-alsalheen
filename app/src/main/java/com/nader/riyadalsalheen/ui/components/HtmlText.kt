package com.nader.riyadalsalheen.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import com.nader.riyadalsalheen.ui.theme.RiyadalsalheenTheme

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp,
    lineHeight: TextUnit = 30.sp
) {
    val spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)

    // Convert HTML to AnnotatedString with proper styling
    val annotatedString = buildAnnotatedString {
        append(spanned.toString())
    }

    Text(
        text = annotatedString,
        modifier = modifier.fillMaxWidth(),
        fontSize = fontSize,
        lineHeight = lineHeight,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun HighlightedText(
    text: String,
    searchQuery: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
    lineHeight: TextUnit = 22.sp,
    maxLines: Int = Int.MAX_VALUE
) {
    val annotatedString = if (searchQuery.isNotBlank()) {
        buildAnnotatedString {
            var currentIndex = 0
            val lowerText = text.lowercase()
            val lowerQuery = searchQuery.lowercase()

            while (currentIndex < text.length) {
                val index = lowerText.indexOf(lowerQuery, currentIndex)
                if (index == -1) {
                    append(text.substring(currentIndex))
                    break
                }

                // Append text before match
                if (index > currentIndex) {
                    append(text.substring(currentIndex, index))
                }

                // Append highlighted match
                withStyle(
                    style = SpanStyle(
                        background = MaterialTheme.colorScheme.primaryContainer,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(text.substring(index, index + lowerQuery.length))
                }

                currentIndex = index + lowerQuery.length
            }
        }
    } else {
        AnnotatedString(text)
    }

    Text(
        text = annotatedString,
        modifier = modifier,
        fontSize = fontSize,
        lineHeight = lineHeight,
        maxLines = maxLines,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
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
fun HtmlTextPreview() {
    RiyadalsalheenTheme {
        HtmlText(
            html = "<p dir=\"rtl\" style=\"text-align:justify\"><span style=\"font-size:18pt\"><span style=\"font-family:tahoma,geneva,sans-serif\"><span style=\"background-color:transparent; color:rgb(0, 0, 0)\">وعَنْ أَميرِ الْمُؤْمِنِينَ أبي حفْصٍ عُمرَ بنِ الْخَطَّابِ بْن نُفَيْل بْنِ عَبْد الْعُزَّى بن رياح بْن عبدِ اللَّهِ بْن قُرْطِ بْنِ رزاح بْنِ عَدِيِّ بْن كَعْبِ بْن لُؤَيِّ بن غالبٍ القُرَشِيِّ العدويِّ . رضي الله عنه ، قال : سمعْتُ رسُولَ الله صَلّى اللهُ عَلَيْهِ وسَلَّم يقُولُ &nbsp;</span><span style=\"background-color:transparent; color:rgb(0, 0, 255)\">&laquo; </span><span style=\"background-color:transparent; color:rgb(0, 0, 255)\">إنَّما</span><span style=\"background-color:transparent; color:rgb(0, 0, 255)\"> الأَعمالُ </span><span style=\"background-color:transparent; color:rgb(0, 0, 255)\">بالنِّيَّات</span><span style=\"background-color:transparent; color:rgb(0, 0, 255)\"> ، وإِنَّمَا لِكُلِّ امرئٍ مَا نَوَى ، فمنْ كانَتْ </span><span style=\"background-color:transparent; color:rgb(0, 0, 255)\">هجْرَتُهُ</span><span style=\"background-color:transparent; color:rgb(0, 0, 255)\"> إِلَى الله ورَسُولِهِ فهجرتُه إلى الله ورسُولِهِ ، ومنْ كاَنْت هجْرَتُه لدُنْيَا يُصيبُها ، أَو امرَأَةٍ يَنْكحُها فهْجْرَتُهُ إلى ما هَاجَر إليْهِ &raquo;</span></span></span></p>\n" +
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
                    "<p>&nbsp;</p>"
        )
    }
}

@Preview(
    name = "Default Light Theme",
    showBackground = true,
    locale = "ar"
)
@Composable
fun HighlightedTextPreview() {
    RiyadalsalheenTheme {
        HighlightedText(
            text = "السلام عليكم ورحمة الله وبركاته",
            searchQuery = "عليكم"
        )
    }
}
