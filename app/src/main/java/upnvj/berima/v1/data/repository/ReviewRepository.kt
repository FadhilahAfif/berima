package upnvj.berima.v1.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import upnvj.berima.v1.data.model.Review
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CRUD for the `reviews` collection. Writing a review also refreshes the
 * rating aggregates on both the listing and the seller profile in a
 * single Firestore transaction.
 */
@Singleton
class ReviewRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val reviewsCollection = firestore.collection("reviews")
    private val listingsCollection = firestore.collection("listings")
    private val usersCollection = firestore.collection("users")
    private val ordersCollection = firestore.collection("orders")

    fun getReviewsForListing(listingId: String): Flow<List<Review>> = callbackFlow {
        val listener = reviewsCollection
            .whereEqualTo("listingId", listingId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val reviews = snapshot?.documents
                    ?.mapNotNull { it.toObject(Review::class.java) }
                    ?: emptyList()
                trySend(reviews)
            }
        awaitClose { listener.remove() }
    }

    fun getReviewsForSeller(sellerId: String): Flow<List<Review>> = callbackFlow {
        val listener = reviewsCollection
            .whereEqualTo("sellerId", sellerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val reviews = snapshot?.documents
                    ?.mapNotNull { it.toObject(Review::class.java) }
                    ?: emptyList()
                trySend(reviews)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Writes the review, marks the order as reviewed, and recomputes the
     * running averages on the listing and the seller — all inside one
     * Firestore transaction so the aggregates never drift.
     */
    suspend fun createReview(review: Review): Result<String> {
        return try {
            val reviewRef = reviewsCollection.document()
            val listingRef = listingsCollection.document(review.listingId)
            val sellerRef = usersCollection.document(review.sellerId)
            val orderRef = ordersCollection.document(review.orderId)

            val newId = reviewRef.id

            firestore.runTransaction { tx ->
                val listingSnap = tx.get(listingRef)
                val sellerSnap = tx.get(sellerRef)

                val listingAvg = listingSnap.getDouble("averageRating") ?: 0.0
                val listingCount = listingSnap.getLong("totalOrders") ?: 0L

                val sellerAvg = sellerSnap.getDouble("averageRating") ?: 0.0
                val sellerReviews = sellerSnap.getLong("totalReviews") ?: 0L

                // New averages. We use totalReviews as the divisor for both so the
                // rating reflects the count of actual reviews, not orders.
                val listingNewCount = (listingSnap.getLong("reviewCount") ?: 0L) + 1L
                val listingNewAvg = ((listingAvg * (listingNewCount - 1)) + review.rating) /
                    listingNewCount.toDouble()

                val sellerNewCount = sellerReviews + 1L
                val sellerNewAvg = ((sellerAvg * sellerReviews) + review.rating) /
                    sellerNewCount.toDouble()

                tx.set(reviewRef, review.copy(reviewId = newId))

                tx.update(
                    listingRef,
                    mapOf(
                        "averageRating" to listingNewAvg,
                        "reviewCount" to listingNewCount
                    )
                )
                tx.update(
                    sellerRef,
                    mapOf(
                        "averageRating" to sellerNewAvg,
                        "totalReviews" to sellerNewCount
                    )
                )
                tx.update(orderRef, "hasReview", true)

                // also suppress unused variable warning
                listingCount
            }.await()

            Result.success(newId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
