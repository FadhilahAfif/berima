package upnvj.berima.v1.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Order document in `orders/{orderId}`.
 * `price` is frozen at creation. `status` uses [OrderStatus] constants.
 */
data class Order(
    @DocumentId val orderId: String = "",
    val listingId: String = "",
    val listingTitle: String = "",
    val buyerId: String = "",
    val buyerName: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val price: Long = 0L,
    val note: String? = null,
    val status: String = OrderStatus.PENDING,
    val attachmentUrl: String? = null,
    val hasReview: Boolean = false,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)
