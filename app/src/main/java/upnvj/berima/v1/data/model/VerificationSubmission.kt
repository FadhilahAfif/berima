package upnvj.berima.v1.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Manual-review document in `verificationSubmissions/{submissionId}`.
 * Private files are referenced by Storage path only, never public URLs.
 */
data class VerificationSubmission(
    @DocumentId val submissionId: String = "",
    val userId: String = "",
    val type: String = VerificationType.IDENTITY,
    val status: String = VerificationStatus.PENDING,
    val documentType: String? = null,
    val skillCategory: String? = null,
    val portfolioItemId: String? = null,
    val externalLink: String? = null,
    val storagePath: String? = null,
    val fileName: String? = null,
    val contentType: String? = null,
    val note: String? = null,
    val rejectionReason: String? = null,
    val reviewedBy: String? = null,
    val reviewedAt: Timestamp? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)
