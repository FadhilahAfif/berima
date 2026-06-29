package upnvj.berima.v1.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import upnvj.berima.v1.data.model.Category
import upnvj.berima.v1.data.model.IdentityDocumentType
import upnvj.berima.v1.data.model.PortfolioItem
import upnvj.berima.v1.data.model.Validation
import upnvj.berima.v1.data.model.VerificationStatus
import upnvj.berima.v1.data.model.VerificationSubmission
import upnvj.berima.v1.data.model.VerificationType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VerificationRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val submissionsCollection = firestore.collection("verificationSubmissions")
    private val portfolioCollection = firestore.collection("portfolioItems")

    fun newSubmissionId(): String = submissionsCollection.document().id

    fun observeSubmissions(userId: String, type: String): Flow<List<VerificationSubmission>> =
        callbackFlow {
            val listener = submissionsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("type", type)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val submissions = snapshot?.documents
                        ?.mapNotNull { it.toObject(VerificationSubmission::class.java) }
                        ?.sortedByDescending { it.createdAt }
                        ?: emptyList()
                    trySend(submissions)
                }
            awaitClose { listener.remove() }
        }

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

    suspend fun createIdentitySubmission(
        submissionId: String,
        userId: String,
        storagePath: String,
        fileName: String?,
        contentType: String?,
        note: String?
    ): Result<String> {
        return try {
            if (hasActiveSubmission(userId, VerificationType.IDENTITY)) {
                return Result.failure(
                    IllegalStateException("Pengajuan identitas sedang diproses atau sudah disetujui")
                )
            }
            val cleanNote = note?.trim().takeUnless { it.isNullOrBlank() }
            if ((cleanNote?.length ?: 0) > Validation.MAX_VERIFICATION_NOTE_LENGTH) {
                return Result.failure(
                    IllegalArgumentException("Catatan maksimal ${Validation.MAX_VERIFICATION_NOTE_LENGTH} karakter")
                )
            }
            val now = Timestamp.now()
            submissionsCollection.document(submissionId).set(
                mapOf(
                    "userId" to userId,
                    "type" to VerificationType.IDENTITY,
                    "status" to VerificationStatus.PENDING,
                    "documentType" to IdentityDocumentType.KTM,
                    "skillCategory" to null,
                    "portfolioItemId" to null,
                    "externalLink" to null,
                    "storagePath" to storagePath,
                    "fileName" to fileName,
                    "contentType" to contentType,
                    "note" to cleanNote,
                    "createdAt" to now,
                    "updatedAt" to now
                )
            ).await()
            Result.success(submissionId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSkillSubmission(
        submissionId: String,
        userId: String,
        category: String,
        portfolioItemId: String?,
        externalLink: String?,
        storagePath: String?,
        fileName: String?,
        contentType: String?
    ): Result<String> {
        return try {
            if (category !in Category.ALL) {
                return Result.failure(IllegalArgumentException("Kategori keahlian tidak valid"))
            }
            if (hasActiveSubmission(userId, VerificationType.SKILL, category)) {
                return Result.failure(
                    IllegalStateException("Pengajuan kategori ini sedang diproses atau sudah disetujui")
                )
            }
            val cleanPortfolioId = portfolioItemId?.takeUnless { it.isBlank() }
            val cleanLink = externalLink?.trim().takeUnless { it.isNullOrBlank() }
            if (cleanPortfolioId == null && cleanLink == null && storagePath == null) {
                return Result.failure(
                    IllegalArgumentException("Tambahkan minimal satu bukti keahlian")
                )
            }
            val now = Timestamp.now()
            submissionsCollection.document(submissionId).set(
                mapOf(
                    "userId" to userId,
                    "type" to VerificationType.SKILL,
                    "status" to VerificationStatus.PENDING,
                    "documentType" to null,
                    "skillCategory" to category,
                    "portfolioItemId" to cleanPortfolioId,
                    "externalLink" to cleanLink,
                    "storagePath" to storagePath,
                    "fileName" to fileName,
                    "contentType" to contentType,
                    "note" to null,
                    "createdAt" to now,
                    "updatedAt" to now
                )
            ).await()
            Result.success(submissionId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun hasActiveSubmission(
        userId: String,
        type: String,
        category: String? = null
    ): Boolean {
        val snapshot = submissionsCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("type", type)
            .get()
            .await()

        return snapshot.documents
            .mapNotNull { it.toObject(VerificationSubmission::class.java) }
            .any { submission ->
                val categoryMatches = category == null || submission.skillCategory == category
                categoryMatches && submission.status in listOf(
                    VerificationStatus.PENDING,
                    VerificationStatus.APPROVED
                )
            }
    }
}
