package com.nader.riyadalsalheen.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nader.riyadalsalheen.R
import com.nader.riyadalsalheen.model.Book
import com.nader.riyadalsalheen.model.Door
import com.nader.riyadalsalheen.ui.theme.RiyadalsalheenTheme

@Composable
fun NavigationBreadcrumb(
    book: Book,
    door: Door,
    onBookClick: () -> Unit = {},
    onDoorClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        ),
        shape = RectangleShape,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.02f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book section with icon
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onBookClick() }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = book.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Separator with custom shape
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_chevron_right_24),
                contentDescription = "Navigate to",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )

            // Door section with icon
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onDoorClick() }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = door.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.tertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    locale = "ar"
)

@Composable
fun NavigationBreadcrumbPreview() {
    RiyadalsalheenTheme {
        NavigationBreadcrumb(
            door= Door(2, 3, "باب تعظيم حرمات المسلمين وبيان حقوقهم والشفقة عليهم ورحمتهم"),
            book= Book(3, "كتاب عيادة المريض وتشييع الميت والصلاة عليه وحضور دفنه والمكث عند قبره بعد دفنه")
        )
    }
}