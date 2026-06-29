package upnvj.berima.v1.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import upnvj.berima.v1.data.model.Category
import upnvj.berima.v1.data.model.PortfolioItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortfolioRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val portfolioCollection = firestore.collection("portfolioItems")

    fun newPortfolioItemId(): String = portfolioCollection.document().id

    fun observePortfolioItems(userId: String): Flow<List<PortfolioItem>> = callbackFlow {
        val listener = portfolioCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents
                    ?.mapNotNull { it.toObject(PortfolioItem::class.java) }
                    ?.sortedByDescending { it.createdAt }
                    ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    suspend fun createPortfolioItem(
        itemId: String,
        userId: String,
        title: String,
        description: String,
        category: String,
        externalLink: String?,
        imageUrl: String?,
        imageStoragePath: String?
    ): Result<String> {
        return try {
            if (category !in Category.ALL) {
                return Result.failure(IllegalArgumentException("Kategori portofolio tidak valid"))
            }
            val now = Timestamp.now()
            val item = PortfolioItem(
                portfolioItemId = itemId,
                userId = userId,
                title = title.trim(),
                description = description.trim(),
                category = category,
                externalLink = externalLink.cleanLink(),
                imageUrl = imageUrl,
                imageStoragePath = imageStoragePath,
                createdAt = now,
                updatedAt = now
            )
            portfolioCollection.document(itemId).set(item).await()
            Result.success(itemId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePortfolioItem(
        itemId: String,
        title: String,
        description: String,
        category: String,
        externalLink: String?,
        imageUrl: String?,
        imageStoragePath: String?,
        removeImage: Boolean
    ): Result<Unit> {
        return try {
            if (category !in Category.ALL) {
                return Result.failure(IllegalArgumentException("Kategori portofolio tidak valid"))
            }
            val updates = mutableMapOf<String, Any?>(
                "title" to title.trim(),
                "description" to description.trim(),
                "category" to category,
                "externalLink" to externalLink.cleanLink(),
                "updatedAt" to Timestamp.now()
            )
            if (imageUrl != null || imageStoragePath != null || removeImage) {
                updates["imageUrl"] = imageUrl
                updates["imageStoragePath"] = imageStoragePath
            }
            portfolioCollection.document(itemId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePortfolioItem(itemId: String): Result<Unit> {
        return try {
            portfolioCollection.document(itemId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun String?.cleanLink(): String? =
        this?.trim()?.takeIf { it.isNotBlank() }
}
