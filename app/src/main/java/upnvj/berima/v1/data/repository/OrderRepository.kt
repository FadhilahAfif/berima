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
    suspend fun markPaid(orderId: String, buyerId: String): Result<Unit> {
        return try {
            firestore.runTransaction { txn ->
                val orderRef = ordersCollection.document(orderId)
                val snapshot = txn.get(orderRef)
                val order = snapshot.toObject(Order::class.java)
                    ?: throw IllegalStateException("Pesanan tidak ditemukan")
                if (order.buyerId != buyerId) throw IllegalStateException("Aksi hanya untuk pemesan")
                if (order.status == OrderStatus.PAID) return@runTransaction null
                if (order.status != OrderStatus.COMPLETED) {
                    throw IllegalStateException("Pesanan belum siap dibayar")
                }

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

    suspend fun acceptOrder(orderId: String, sellerId: String): Result<Unit> =
        transition(orderId, OrderStatus.PENDING, OrderStatus.IN_PROGRESS, sellerId, buyerAction = false)

    suspend fun rejectOrder(orderId: String, sellerId: String): Result<Unit> =
        transition(orderId, OrderStatus.PENDING, OrderStatus.REJECTED, sellerId, buyerAction = false)

    suspend fun cancelOrder(orderId: String, buyerId: String): Result<Unit> =
        transition(orderId, OrderStatus.PENDING, OrderStatus.CANCELLED, buyerId, buyerAction = true)

    suspend fun confirmDelivered(orderId: String, buyerId: String): Result<Unit> =
        transition(orderId, OrderStatus.DELIVERED, OrderStatus.COMPLETED, buyerId, buyerAction = true)

    suspend fun requestRevision(orderId: String, buyerId: String, note: String): Result<Unit> {
        return try {
            firestore.runTransaction { txn ->
                val orderRef = ordersCollection.document(orderId)
                val order = txn.get(orderRef).toObject(Order::class.java)
                    ?: throw IllegalStateException("Pesanan tidak ditemukan")
                if (order.buyerId != buyerId) throw IllegalStateException("Aksi hanya untuk pemesan")
                if (order.status != OrderStatus.DELIVERED) throw IllegalStateException("Hasil belum bisa direvisi")
                if (order.revisionCount >= 1L) throw IllegalStateException("Revisi hanya tersedia satu kali")
                txn.update(
                    orderRef,
                    mapOf(
                        "status" to OrderStatus.REVISION_REQUESTED,
                        "revisionNote" to note,
                        "revisionCount" to FieldValue.increment(1L),
                        "updatedAt" to Timestamp.now()
                    )
                )
                null
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setRequirementFile(
        orderId: String,
        buyerId: String,
        metadata: OrderFileUploadMetadata
    ): Result<Unit> {
        return try {
            firestore.runTransaction { txn ->
                val orderRef = ordersCollection.document(orderId)
                val order = txn.get(orderRef).toObject(Order::class.java)
                    ?: throw IllegalStateException("Pesanan tidak ditemukan")
                if (order.buyerId != buyerId) throw IllegalStateException("Aksi hanya untuk pemesan")
                txn.update(
                    orderRef,
                    mapOf(
                        "requirementFileUrl" to metadata.downloadUrl,
                        "requirementFileName" to metadata.fileName,
                        "requirementStoragePath" to metadata.storagePath,
                        "updatedAt" to Timestamp.now()
                    )
                )
                null
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setResultFile(
        orderId: String,
        sellerId: String,
        metadata: OrderFileUploadMetadata
    ): Result<Unit> {
        return try {
            firestore.runTransaction { txn ->
                val orderRef = ordersCollection.document(orderId)
                val order = txn.get(orderRef).toObject(Order::class.java)
                    ?: throw IllegalStateException("Pesanan tidak ditemukan")
                if (order.sellerId != sellerId) throw IllegalStateException("Aksi hanya untuk penyedia jasa")
                if (order.status !in listOf(OrderStatus.IN_PROGRESS, OrderStatus.REVISION_REQUESTED)) {
                    throw IllegalStateException("Hasil belum bisa dikirim")
                }
                txn.update(
                    orderRef,
                    mapOf(
                        "attachmentUrl" to metadata.downloadUrl,
                        "resultFileName" to metadata.fileName,
                        "resultStoragePath" to metadata.storagePath,
                        "status" to OrderStatus.DELIVERED,
                        "updatedAt" to Timestamp.now()
                    )
                )
                null
            }.await()
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

    private suspend fun transition(
        orderId: String,
        fromStatus: String,
        toStatus: String,
        actorId: String,
        buyerAction: Boolean
    ): Result<Unit> {
        return try {
            firestore.runTransaction { txn ->
                val orderRef = ordersCollection.document(orderId)
                val order = txn.get(orderRef).toObject(Order::class.java)
                    ?: throw IllegalStateException("Pesanan tidak ditemukan")
                val allowedActor = if (buyerAction) order.buyerId else order.sellerId
                if (allowedActor != actorId) {
                    throw IllegalStateException(
                        if (buyerAction) "Aksi hanya untuk pemesan" else "Aksi hanya untuk penyedia jasa"
                    )
                }
                if (order.status != fromStatus) throw IllegalStateException("Status pesanan sudah berubah")
                txn.update(
                    orderRef,
                    mapOf(
                        "status" to toStatus,
                        "updatedAt" to Timestamp.now()
                    )
                )
                null
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
