package upnvj.berima.v1.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import upnvj.berima.v1.data.model.Listing
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CRUD + queries for the `listings` collection.
 * Denormalized seller fields (name, photo, rating) are written at create time.
 */
@Singleton
class ListingRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val listingsCollection = firestore.collection("listings")

    fun getActiveListings(limit: Long = 20L): Flow<List<Listing>> = callbackFlow {
        val listener = listingsCollection
            .whereEqualTo("isActive", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val listings = snapshot?.documents
                    ?.mapNotNull { it.toObject(Listing::class.java) }
                    ?: emptyList()
                trySend(listings)
            }
        awaitClose { listener.remove() }
    }

    fun getListingsByCategory(category: String, limit: Long = 20L): Flow<List<Listing>> =
        callbackFlow {
            val listener = listingsCollection
                .whereEqualTo("isActive", true)
                .whereEqualTo("category", category)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val listings = snapshot?.documents
                        ?.mapNotNull { it.toObject(Listing::class.java) }
                        ?: emptyList()
                    trySend(listings)
                }
            awaitClose { listener.remove() }
        }

    fun getFeaturedListings(limit: Long = 5L): Flow<List<Listing>> = callbackFlow {
        val listener = listingsCollection
            .whereEqualTo("isActive", true)
            .orderBy("totalOrders", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val listings = snapshot?.documents
                    ?.mapNotNull { it.toObject(Listing::class.java) }
                    ?: emptyList()
                trySend(listings)
            }
        awaitClose { listener.remove() }
    }

    fun getListingsBySeller(sellerId: String): Flow<List<Listing>> = callbackFlow {
        val listener = listingsCollection
            .whereEqualTo("sellerId", sellerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val listings = snapshot?.documents
                    ?.mapNotNull { it.toObject(Listing::class.java) }
                    ?: emptyList()
                trySend(listings)
            }
        awaitClose { listener.remove() }
    }

    fun observeListing(listingId: String): Flow<Listing?> = callbackFlow {
        val listener = listingsCollection.document(listingId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(Listing::class.java))
            }
        awaitClose { listener.remove() }
    }

    suspend fun getListing(listingId: String): Result<Listing?> {
        return try {
            val snapshot = listingsCollection.document(listingId).get().await()
            Result.success(snapshot.toObject(Listing::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Creates a new listing. The document ID is generated client-side so we
     * can store it inside the document as `listingId`.
     */
    suspend fun createListing(listing: Listing): Result<String> {
        return try {
            val ref = listingsCollection.document()
            ref.set(listing.copy(listingId = ref.id)).await()
            Result.success(ref.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateListing(
        listingId: String,
        title: String,
        description: String,
        category: String,
        price: Long,
        deliveryTimeHours: Int,
        tags: List<String>,
        thumbnailUrl: String?,
        sellerIdentityVerified: Boolean? = null,
        sellerVerifiedSkillBadges: List<String>? = null
    ): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any?>(
                "title" to title,
                "description" to description,
                "category" to category,
                "price" to price,
                "deliveryTimeHours" to deliveryTimeHours,
                "tags" to tags
            )
            if (thumbnailUrl != null) {
                updates["thumbnailUrl"] = thumbnailUrl
            }
            sellerIdentityVerified?.let {
                updates["sellerIdentityVerified"] = it
            }
            sellerVerifiedSkillBadges?.let {
                updates["sellerVerifiedSkillBadges"] = it
            }
            listingsCollection.document(listingId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setListingActive(listingId: String, isActive: Boolean): Result<Unit> {
        return try {
            listingsCollection.document(listingId)
                .update("isActive", isActive)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Increment `totalOrders` when a new order is created against this listing. */
    internal suspend fun incrementTotalOrders(listingId: String): Result<Unit> {
        return try {
            listingsCollection.document(listingId)
                .update("totalOrders", FieldValue.increment(1L))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
