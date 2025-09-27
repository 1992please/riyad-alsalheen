package com.nader.riyadalsalheen.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp,
    lineHeight: TextUnit = 30.sp,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = LocalTextStyle.current
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
        color = color,
        style = style
    )
}