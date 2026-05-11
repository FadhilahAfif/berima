package upnvj.berima.v1.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import upnvj.berima.v1.data.model.Order
import upnvj.berima.v1.data.model.OrderStatus
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CRUD + queries for the `orders` collection. Also bumps denormalized
 * counters on the related `users` and `listings` documents.
 */
@Singleton
class OrderRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val ordersCollection = firestore.collection("orders")
    private val usersCollection = firestore.collection("users")
    private val listingsCollection = firestore.collection("listings")

    fun getOrdersAsBuyer(buyerId: String): Flow<List<Order>> = callbackFlow {
        val listener = ordersCollection
            .whereEqualTo("buyerId", buyerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.documents
                    ?.mapNotNull { it.toObject(Order::class.java) }
                    ?: emptyList()
                trySend(orders)
            }
        awaitClose { listener.remove() }
    }

    fun getOrdersAsSeller(sellerId: String): Flow<List<Order>> = callbackFlow {
        val listener = ordersCollection
            .whereEqualTo("sellerId", sellerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.documents
                    ?.mapNotNull { it.toObject(Order::class.java) }
                    ?: emptyList()
                trySend(orders)
            }
        awaitClose { listener.remove() }
    }

    fun observeOrder(orderId: String): Flow<Order?> = callbackFlow {
        val listener = ordersCollection.document(orderId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(Order::class.java))
            }
        awaitClose { listener.remove() }
    }

    suspend fun getOrder(orderId: String): Result<Order?> {
        return try {
            val snapshot = ordersCollection.document(orderId).get().await()
            Result.success(snapshot.toObject(Order::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Creates the order, then bumps `totalOrdersAsBuyer`, `totalOrdersAsSeller`
     * on the users and `totalOrders` on the listing. Counter writes are
     * best-effort; a failure there does not roll back the order.
     */
    suspend fun createOrder(order: Order): Result<String> {
        return try {
            val ref = ordersCollection.document()
            val now = Timestamp.now()
            val toWrite = order.copy(
                orderId = ref.id,
                status = OrderStatus.PENDING,
                createdAt = now,
                updatedAt = now
            )
            ref.set(toWrite).await()

            runCatching {
                usersCollection.document(order.buyerId)
                    .update("totalOrdersAsBuyer", FieldValue.increment(1L))
                    .await()
            }
            runCatching {
                usersCollection.document(order.sellerId)
                    .update("totalOrdersAsSeller", FieldValue.increment(1L))
                    .await()
            }
            runCatching {
                listingsCollection.document(order.listingId)
                    .update("totalOrders", FieldValue.increment(1L))
                    .await()
            }

            Result.success(ref.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStatus(orderId: String, status: String): Result<Unit> {
        return try {
            ordersCollection.document(orderId)
                .update(
                    mapOf(
                        "status" to status,
                        "updatedAt" to Timestamp.now()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setAttachmentUrl(orderId: String, url: String): Result<Unit> {
        return try {
            ordersCollection.document(orderId)
                .update(
                    mapOf(
                        "attachmentUrl" to url,
                        "status" to OrderStatus.DELIVERED,
                        "updatedAt" to Timestamp.now()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markHasReview(orderId: String): Result<Unit> {
        return try {
            ordersCollection.document(orderId)
                .update("hasReview", true)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
