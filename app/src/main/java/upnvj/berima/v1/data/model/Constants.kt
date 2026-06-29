package upnvj.berima.v1.data.model

/**
 * Shared constant values used across collections.
 * Keep these in sync with .agents/context/database.md and features.md.
 */

object UserRole {
    const val BUYER = "buyer"
    const val SELLER = "seller"
    const val BOTH = "both"
}

object Category {
    const val ACADEMIC = "academic"
    const val VISUAL = "visual"
    const val DATA = "data"

    val ALL = listOf(ACADEMIC, VISUAL, DATA)
}

object VerificationStatus {
    const val NOT_SUBMITTED = "not_submitted"
    const val PENDING = "pending"
    const val APPROVED = "approved"
    const val REJECTED = "rejected"

    val ALL = listOf(NOT_SUBMITTED, PENDING, APPROVED, REJECTED)
}

object VerificationType {
    const val IDENTITY = "identity"
    const val SKILL = "skill"
}

object IdentityDocumentType {
    const val KTM = "ktm"
}

object OrderStatus {
    const val PENDING = "pending"
    const val IN_PROGRESS = "in_progress"
    const val DELIVERED = "delivered"
    const val REVISION_REQUESTED = "revision_requested"
    const val COMPLETED = "completed"
    const val PAID = "paid"
    const val CANCELLED = "cancelled"
    const val REJECTED = "rejected"
}

object Validation {
    const val MIN_PASSWORD_LENGTH = 8

    const val MAX_LISTING_TITLE_LENGTH = 60
    const val MAX_LISTING_DESCRIPTION_LENGTH = 500
    const val MAX_DELIVERY_TIME_HOURS = 48

    const val MAX_ORDER_NOTE_LENGTH = 300
    const val MAX_REVIEW_COMMENT_LENGTH = 300
    const val MAX_MESSAGE_LENGTH = 500
    const val MAX_NAME_LENGTH = 50
    const val MAX_BIO_LENGTH = 150
    const val MAX_PORTFOLIO_TITLE_LENGTH = 60
    const val MAX_PORTFOLIO_DESCRIPTION_LENGTH = 500
    const val MAX_VERIFICATION_NOTE_LENGTH = 300
    const val MAX_STORAGE_IMAGE_BYTES = 5L * 1024 * 1024
    const val MAX_VERIFICATION_FILE_BYTES = 20L * 1024 * 1024

    fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
}
