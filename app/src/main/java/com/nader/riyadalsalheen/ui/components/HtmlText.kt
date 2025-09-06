package com.nader.riyadalsalheen.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.core.text.HtmlCompat
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier) {
    val spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)

    Text(
    text = spanned.toString(),
    modifier = modifier.fillMaxWidth(),
    textAlign = TextAlign.End,
    fontSize = 18.sp,
    lineHeight = 30.sp
    )
}