package upnvj.berima.v1.ui.order.components

import androidx.compose.foundation.background
import upnvj.berima.v1.ui.common.AppStrings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import upnvj.berima.v1.data.model.OrderStatus
import upnvj.berima.v1.ui.theme.LocalBerimaColors

/**
 * Stateless 5-dot timeline for the happy-path order flow. Renders a single
 * muted row when the order ended in a terminal failure state
 * (`cancelled` / `rejected`).
 */
@Composable
fun OrderStatusTimeline(
    currentStatus: String,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current

    if (currentStatus == OrderStatus.CANCELLED || currentStatus == OrderStatus.REJECTED) {
        val label = if (currentStatus == OrderStatus.CANCELLED) AppStrings.TIMELINE_CANCELLED else AppStrings.TIMELINE_REJECTED
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val steps = listOf(
        OrderStatus.PENDING to AppStrings.TIMELINE_PENDING,
        OrderStatus.IN_PROGRESS to AppStrings.TIMELINE_IN_PROGRESS,
        OrderStatus.DELIVERED to AppStrings.TIMELINE_DELIVERED,
        OrderStatus.REVISION_REQUESTED to AppStrings.TIMELINE_REVISION,
        OrderStatus.COMPLETED to AppStrings.TIMELINE_COMPLETED,
        OrderStatus.PAID to AppStrings.TIMELINE_PAID
    )
    val currentIndex = steps.indexOfFirst { it.first == currentStatus }
        .let { if (it < 0) 0 else it }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEachIndexed { index, _ ->
                val reached = index <= currentIndex
                val isCurrent = index == currentIndex
                val dotColor = if (reached) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                }
                if (isCurrent) {
                    // "You are here": a soft halo ring around the filled dot.
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(berimaColors.containerGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                }
                if (index < steps.lastIndex) {
                    val lineColor = if (index < currentIndex) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(lineColor)
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            steps.forEachIndexed { index, step ->
                val reached = index <= currentIndex
                Text(
                    text = step.second,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (reached) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        berimaColors.textSecondary
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "OrderStatusTimeline · all happy-path", showBackground = true, backgroundColor = 0xFFF2EFE9)
@Composable
private fun OrderStatusTimelinePreview() {
    upnvj.berima.v1.ui.theme.BerimaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OrderStatusTimeline(currentStatus = OrderStatus.PENDING)
            OrderStatusTimeline(currentStatus = OrderStatus.IN_PROGRESS)
            OrderStatusTimeline(currentStatus = OrderStatus.DELIVERED)
            OrderStatusTimeline(currentStatus = OrderStatus.REVISION_REQUESTED)
            OrderStatusTimeline(currentStatus = OrderStatus.COMPLETED)
            OrderStatusTimeline(currentStatus = OrderStatus.PAID)
            OrderStatusTimeline(currentStatus = OrderStatus.CANCELLED)
            OrderStatusTimeline(currentStatus = OrderStatus.REJECTED)
        }
    }
}
