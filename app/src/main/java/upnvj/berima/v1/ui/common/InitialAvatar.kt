package upnvj.berima.v1.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import upnvj.berima.v1.ui.theme.LocalBerimaColors
import java.util.Locale

/**
 * Circular monogram for a person. Orders are person-to-person and the [Order]
 * model carries no counterparty photo, so the counterparty's first initial in a
 * single brand-tinted circle gives every order a human identity without a per-name
 * color (keeps the palette restrained). Used by the orders list and the order
 * detail counterparty row.
 *
 * Per DESIGN.md: circular shape differentiates people from service listings.
 */
@Composable
fun InitialAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    val berimaColors = LocalBerimaColors.current
    val initial = name.trim().firstOrNull()
        ?.uppercase(Locale("id", "ID"))
        ?: "?"

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(berimaColors.containerGreen),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = (size.value * 0.4f).sp
            ),
            color = MaterialTheme.colorScheme.primary
        )
    }
}
