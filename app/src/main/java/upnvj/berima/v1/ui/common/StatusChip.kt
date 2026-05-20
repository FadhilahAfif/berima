package upnvj.berima.v1.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import upnvj.berima.v1.data.model.OrderStatus
import upnvj.berima.v1.ui.theme.LocalBerimaColors
import upnvj.berima.v1.ui.theme.StatusColors

/**
 * Pill-shaped status indicator. Maps an [OrderStatus] string to its
 * design-system label and color pair. Always renders ALL CAPS.
 */
@Composable
fun StatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val (colors, label) = when (status) {
        OrderStatus.PENDING -> berimaColors.statusPending to "MENUNGGU"
        OrderStatus.IN_PROGRESS -> berimaColors.statusInProgress to "DIKERJAKAN"
        OrderStatus.DELIVERED -> berimaColors.statusDelivered to "TERKIRIM"
        OrderStatus.COMPLETED -> berimaColors.statusCompleted to "SELESAI"
        OrderStatus.PAID -> berimaColors.statusPaid to "DIBAYAR"
        OrderStatus.CANCELLED -> berimaColors.statusCancelled to "DIBATALKAN"
        OrderStatus.REJECTED -> berimaColors.statusRejected to "DITOLAK"
        else -> StatusColors(
            container = MaterialTheme.colorScheme.surfaceContainerHigh,
            text = MaterialTheme.colorScheme.onSurfaceVariant
        ) to status.uppercase()
    }

    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
        color = colors.text,
        modifier = modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(colors.container)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@androidx.compose.ui.tooling.preview.Preview(name = "StatusChip · all states", showBackground = true, backgroundColor = 0xFFF2EFE9)
@Composable
private fun StatusChipPreview() {
    upnvj.berima.v1.ui.theme.BerimaTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            StatusChip(OrderStatus.PENDING)
            StatusChip(OrderStatus.IN_PROGRESS)
            StatusChip(OrderStatus.DELIVERED)
            StatusChip(OrderStatus.COMPLETED)
            StatusChip(OrderStatus.PAID)
            StatusChip(OrderStatus.CANCELLED)
            StatusChip(OrderStatus.REJECTED)
        }
    }
}
