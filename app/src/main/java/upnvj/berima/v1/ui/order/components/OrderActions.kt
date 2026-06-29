package upnvj.berima.v1.ui.order.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import upnvj.berima.v1.data.model.OrderStatus
import upnvj.berima.v1.ui.common.AppStrings
import upnvj.berima.v1.ui.common.BerimaButton
import upnvj.berima.v1.ui.common.DangerActionButton
import upnvj.berima.v1.ui.order.OrderAction
import upnvj.berima.v1.ui.theme.LocalBerimaColors

/**
 * Renders the action buttons for the order detail screen, driven by
 * `(status, isBuyer, hasReview)`. The composable is dumb: it only emits
 * [OrderAction]s through [onAction] (or invokes [onPickFile] for the
 * one action that needs the screen-owned file picker launcher).
 */
@Composable
fun OrderActions(
    status: String,
    isBuyer: Boolean,
    hasReview: Boolean,
    actionInFlight: String?,
    onAction: (OrderAction) -> Unit,
    onPickFile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading = actionInFlight != null
    val berimaColors = LocalBerimaColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        when (status) {
            OrderStatus.PENDING -> {
                if (isBuyer) {
                    DangerActionButton(
                        text = AppStrings.ORDER_ACTION_CANCEL,
                        onClick = { onAction(OrderAction.Cancel) },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DangerActionButton(
                            text = AppStrings.ORDER_ACTION_REJECT,
                            onClick = { onAction(OrderAction.Reject) },
                            enabled = !isLoading,
                            modifier = Modifier
                                .weight(1f)
                        )
                        BerimaButton(
                            text = AppStrings.ORDER_ACTION_ACCEPT,
                            onClick = { onAction(OrderAction.Accept) },
                            isLoading = isLoading,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            OrderStatus.IN_PROGRESS -> {
                if (isBuyer) {
                    Callout(text = AppStrings.ORDER_ACTION_WAITING_RESULT)
                } else {
                    BerimaButton(
                        text = AppStrings.ORDER_ACTION_UPLOAD_RESULT,
                        onClick = onPickFile,
                        isLoading = isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            OrderStatus.DELIVERED -> {
                if (isBuyer) {
                    BerimaButton(
                        text = AppStrings.ORDER_ACTION_CONFIRM_DONE,
                        onClick = { onAction(OrderAction.ConfirmDelivered) },
                        isLoading = isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Callout(text = AppStrings.ORDER_ACTION_WAITING_BUYER_CONFIRM)
                }
            }

            OrderStatus.COMPLETED -> {
                if (isBuyer) {
                    BerimaButton(
                        text = AppStrings.ORDER_ACTION_SIMULATE_PAY,
                        onClick = { onAction(OrderAction.SimulatePay) },
                        isLoading = isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = AppStrings.ORDER_ACTION_SIMULATE_PAY_HELP,
                        style = MaterialTheme.typography.labelSmall,
                        color = berimaColors.textSecondary
                    )
                } else {
                    Callout(text = AppStrings.ORDER_ACTION_WAITING_PAYMENT)
                }
            }

            OrderStatus.PAID -> {
                if (isBuyer && !hasReview) {
                    BerimaButton(
                        text = AppStrings.ORDER_ACTION_WRITE_REVIEW,
                        onClick = { onAction(OrderAction.OpenReview) },
                        isLoading = isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                // Otherwise: nothing (seller, or buyer who already reviewed).
            }

            OrderStatus.CANCELLED, OrderStatus.REJECTED -> {
                // Terminal failure: no actions available.
            }
        }
    }
}

@Composable
private fun Callout(
    text: String,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "OrderActions · matrix", showBackground = true, backgroundColor = 0xFFF2EFE9)
@Composable
private fun OrderActionsPreview() {
    upnvj.berima.v1.ui.theme.BerimaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Pemesan · pending", style = MaterialTheme.typography.labelSmall)
            OrderActions(OrderStatus.PENDING, isBuyer = true, hasReview = false, actionInFlight = null, onAction = {}, onPickFile = {})
            Text("Penyedia · pending", style = MaterialTheme.typography.labelSmall)
            OrderActions(OrderStatus.PENDING, isBuyer = false, hasReview = false, actionInFlight = null, onAction = {}, onPickFile = {})
            Text("Penyedia · in_progress", style = MaterialTheme.typography.labelSmall)
            OrderActions(OrderStatus.IN_PROGRESS, isBuyer = false, hasReview = false, actionInFlight = null, onAction = {}, onPickFile = {})
            Text("Pemesan · in_progress", style = MaterialTheme.typography.labelSmall)
            OrderActions(OrderStatus.IN_PROGRESS, isBuyer = true, hasReview = false, actionInFlight = null, onAction = {}, onPickFile = {})
            Text("Pemesan · completed", style = MaterialTheme.typography.labelSmall)
            OrderActions(OrderStatus.COMPLETED, isBuyer = true, hasReview = false, actionInFlight = null, onAction = {}, onPickFile = {})
            Text("Pemesan · paid (no review yet)", style = MaterialTheme.typography.labelSmall)
            OrderActions(OrderStatus.PAID, isBuyer = true, hasReview = false, actionInFlight = null, onAction = {}, onPickFile = {})
        }
    }
}
