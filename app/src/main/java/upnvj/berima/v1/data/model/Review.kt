package upnvj.berima.v1.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Review document in `reviews/{reviewId}`. One per order.
 * Cannot be edited after submission.
 */
data class Review(
    @DocumentId val reviewId: String = "",
    val orderId: String = "",
    val listingId: String = "",
    val buyerId: String = "",
    val buyerName: String = "",
    val buyerPhotoUrl: String? = null,
    val sellerId: String = "",
    val rating: Int = 0,
    val comment: String? = null,
    val createdAt: Timestamp = Timestamp.now()
)
