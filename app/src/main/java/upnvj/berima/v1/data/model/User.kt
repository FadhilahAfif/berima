package upnvj.berima.v1.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * User profile document in `users/{uid}`.
 * The document ID equals the Firebase Auth UID.
 */
data class User(
    @DocumentId val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val bio: String? = null,
    val faculty: String? = null,
    val role: String = UserRole.BUYER,
    val identityVerificationStatus: String = VerificationStatus.NOT_SUBMITTED,
    val isIdentityVerified: Boolean = false,
    val verifiedSkillBadges: List<String> = emptyList(),
    val verificationUpdatedAt: Timestamp? = null,
    val averageRating: Double = 0.0,
    val totalReviews: Int = 0,
    val totalOrdersAsBuyer: Int = 0,
    val totalOrdersAsSeller: Int = 0,
    val createdAt: Timestamp = Timestamp.now()
)
