package upnvj.berima.v1.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Listing document in `listings/{listingId}`.
 * Seller-side denormalized fields (sellerName, sellerPhotoUrl, sellerRating)
 * are written at create time and refreshed by background jobs when needed.
 */
data class Listing(
    @DocumentId val listingId: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val sellerPhotoUrl: String? = null,
    val sellerRating: Double = 0.0,
    val title: String = "",
    val description: String = "",
    val category: String = Category.ACADEMIC,
    val price: Long = 0L,
    val deliveryTimeHours: Int = 24,
    val thumbnailUrl: String? = null,
    val thumbnailStoragePath: String? = null,
    val tags: List<String> = emptyList(),
    val isActive: Boolean = true,
    val sellerIdentityVerified: Boolean = false,
    val sellerVerifiedSkillBadges: List<String> = emptyList(),
    val policyAcceptedAt: Timestamp? = null,
    val averageRating: Double = 0.0,
    val totalOrders: Int = 0,
    val createdAt: Timestamp = Timestamp.now()
)
