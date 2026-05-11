package upnvj.berima.v1.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import upnvj.berima.v1.data.model.Message
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Chat messages scoped to an order: `messages/{orderId}/chats/{messageId}`.
 * Buyer and seller on the order are the only participants.
 */
@Singleton
class MessageRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val messagesRoot = firestore.collection("messages")

    private fun chatRef(orderId: String) =
        messagesRoot.document(orderId).collection("chats")

    fun observeMessages(orderId: String): Flow<List<Message>> = callbackFlow {
        val listener = chatRef(orderId)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents
                    ?.mapNotNull { it.toObject(Message::class.java) }
                    ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(
        orderId: String,
        senderId: String,
        senderName: String,
        text: String
    ): Result<String> {
        if (text.isBlank()) {
            return Result.failure(IllegalArgumentException("Pesan tidak boleh kosong"))
        }
        return try {
            val ref = chatRef(orderId).document()
            val message = Message(
                messageId = ref.id,
                senderId = senderId,
                senderName = senderName,
                text = text.trim(),
                isRead = false,
                createdAt = Timestamp.now()
            )
            ref.set(message).await()
            Result.success(ref.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markRead(orderId: String, messageId: String): Result<Unit> {
        return try {
            chatRef(orderId).document(messageId)
                .update("isRead", true)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
