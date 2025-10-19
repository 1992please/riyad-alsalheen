package com.nader.riyadalsalheen.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat

@Composable
fun HtmlText(
    htmlText: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp,
    lineHeight: TextUnit = 30.sp,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = LocalTextStyle.current
) {
// Sharh P1 (Bright Red)
    val redStyle = SpanStyle(
        fontWeight = FontWeight.Bold,
        color = Color(0xFF_FF0000)
    )

    // Sharh P2 (Dark Red)
    val darkRedStyle = SpanStyle(
        fontWeight = FontWeight.Bold,
        color = Color(0xFF_980000)
    )

    // Hadith P1 (Blue)
    val blueStyle = SpanStyle(
        fontWeight = FontWeight.Bold,
        color = Color(0xFF_00008B) // A dark blue, for example
    )

    // Hadith P2 (Green)
    val greenStyle = SpanStyle(
        color = Color(0xFF_006400) // A dark green
    )

    // Regex to split the string by all known tags, keeping the tags
    val tagPattern = "(<red>|</red>|<darkred>|</darkred>|<blue>|</blue>|<green>|</green>|\\n)"
    val regex = Regex("(?=${tagPattern})|(?<=${tagPattern})")
    val tokens = htmlText.split(regex).filter { it.isNotEmpty() }

    val annotatedString = buildAnnotatedString {
        // These booleans track the *current* style state
        var isRed = false
        var isDarkRed = false
        var isBlue = false
        var isGreen = false

        for (token in tokens) {
            when (token) {
                // Update state on open tags
                "<red>" -> isRed = true
                "<darkred>" -> isDarkRed = true
                "<blue>" -> isBlue = true
                "<green>" -> isGreen = true
                "</red>" -> isRed = false
                "</darkred>" -> isDarkRed = false
                "</blue>" -> isBlue = false
                "</green>" -> isGreen = false
                // Handle newlines
                "\n" -> append("\n")
                // Handle a text token
                else -> {
                    // --- 3. Build the combined style for this token ---
                    var currentStyle = SpanStyle()

                    // Apply styles in order of *lowest* to *highest* priority
                    // to ensure the correct style "wins" any overlap.

                    // Hadith P1 (Lowest priority, just changes font style)
                    if (isBlue) currentStyle = currentStyle.merge(blueStyle)

                    // Hadith P2 (Overwrites blue color if nested)
                    if (isGreen) currentStyle = currentStyle.merge(greenStyle)

                    // Sharh P2 (Bold + Dark Red)
                    if (isDarkRed) currentStyle = currentStyle.merge(darkRedStyle)

                    // Sharh P1 (Bold + Bright Red, will overwrite Dark Red if nested)
                    if (isRed) currentStyle = currentStyle.merge(redStyle)

                    // Append the text with the final, merged style
                    withStyle(style = currentStyle) {
                        append(token)
                    }
                }
            }
        }
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