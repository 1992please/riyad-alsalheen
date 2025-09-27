package com.nader.riyadalsalheen.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    // Use FlowRow for a responsive layout that wraps content if needed.
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp), // A bit less padding as chips have their own.
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically
    ) {
        // Book Chip
        BreadcrumbChip(
            text = book.title,
            iconRes = R.drawable.ic_menu_book_24, // Example icon
            onClick = onBookClick
        )

        // Separator Icon
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_chevron_right_24),
            contentDescription = "Navigate to",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Door Chip
        BreadcrumbChip(
            text = door.title,
            iconRes = R.drawable.ic_door_front_24, // Example icon
            onClick = onDoorClick
        )
    }
}

@Composable
private fun BreadcrumbChip(
    text: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    // AssistChip is the semantically correct component for this action.
    AssistChip(
        onClick = onClick,
        label = {
            // AnimatedContent provides a nice transition when the text changes.
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        },
        leadingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(AssistChipDefaults.IconSize)
            )
        },
        // Use subtle colors that match the app theme.
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        )
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
fun NavigationBreadcrumbPreview() {
    RiyadalsalheenTheme {
        Surface {
            NavigationBreadcrumb(
                door= Door(2, 3, "باب تعظيم حرمات المسلمين وبيان حقوقهم والشفقة عليهم ورحمتهم"),
                book= Book(3, "كتاب عيادة المريض وتشييع الميت والصلاة عليه وحضور دفنه والمكث عند قبره بعد دفنه")
            )
        }
    }
}