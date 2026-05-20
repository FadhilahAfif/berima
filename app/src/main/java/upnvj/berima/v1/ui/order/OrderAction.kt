package upnvj.berima.v1.ui.order

import android.net.Uri

/**
 * User-driven actions on an order. The composable decides which actions
 * are visible based on `(order.status, isBuyer)`; the ViewModel handles
 * dispatch. Keeps the role × status matrix in one place.
 */
sealed interface OrderAction {
    /** Seller @ pending → in_progress. */
    data object Accept : OrderAction

    /** Seller @ pending → rejected. */
    data object Reject : OrderAction

    /** Buyer @ pending → cancelled. */
    data object Cancel : OrderAction

    /** Seller @ in_progress: upload result file then flip to delivered. */
    data class UploadResult(val uri: Uri) : OrderAction

    /** Buyer @ delivered → completed. */
    data object ConfirmDelivered : OrderAction

    /** Buyer @ completed → paid (transactional, bumps counters). */
    data object SimulatePay : OrderAction

    /** Buyer @ paid && !hasReview: open the review composer. */
    data object OpenReview : OrderAction
}
