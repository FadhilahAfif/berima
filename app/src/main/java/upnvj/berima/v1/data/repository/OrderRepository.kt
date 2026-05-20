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
     * Creates the order in `pending` state. Counter increments on
     * `users.totalOrdersAsBuyer`, `users.totalOrdersAsSeller`, and
     * `listings.totalOrders` happen later inside [markPaid] so they only
     * reflect actually-completed transactions.
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
            Result.success(ref.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Atomically flips the order to `paid` and bumps the three denormalized
     * counters (buyer, seller, listing). All four writes happen inside a
     * Firestore transaction so they either all land or none do.
     *
     * Idempotent: if the order is already `paid`, returns success without
     * any writes — guards against accidental double-tap on "Simulasi Bayar".
     */
    suspend fun markPaid(orderId: String): Result<Unit> {
        return try {
            firestore.runTransaction { txn ->
                val orderRef = ordersCollection.document(orderId)
                val snapshot = txn.get(orderRef)
                val order = snapshot.toObject(Order::class.java)
                    ?: throw IllegalStateException("Pesanan tidak ditemukan")
                if (order.status == OrderStatus.PAID) return@runTransaction null

                txn.update(
                    orderRef,
                    mapOf(
                        "status" to OrderStatus.PAID,
                        "updatedAt" to Timestamp.now()
                    )
                )
                txn.update(
                    usersCollection.document(order.buyerId),
                    "totalOrdersAsBuyer",
                    FieldValue.increment(1L)
                )
                txn.update(
                    usersCollection.document(order.sellerId),
                    "totalOrdersAsSeller",
                    FieldValue.increment(1L)
                )
                txn.update(
                    listingsCollection.document(order.listingId),
                    "totalOrders",
                    FieldValue.increment(1L)
                )
                null
            }.await()
            Result.success(Unit)
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
