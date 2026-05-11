package upnvj.berima.v1.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Chat message document in `messages/{orderId}/chats/{messageId}`.
 * Messages are scoped to a single order between its buyer and seller.
 */
data class Message(
    @DocumentId val messageId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val isRead: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
)
