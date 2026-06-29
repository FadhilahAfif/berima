package upnvj.berima.v1.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import upnvj.berima.v1.data.model.Validation
import javax.inject.Inject
import javax.inject.Singleton

data class StorageUploadMetadata(
    val storagePath: String,
    val fileName: String?,
    val contentType: String?
)

data class PublicImageUploadMetadata(
    val downloadUrl: String,
    val storagePath: String,
    val fileName: String?,
    val contentType: String?
)

data class OrderFileUploadMetadata(
    val downloadUrl: String,
    val storagePath: String,
    val fileName: String?,
    val contentType: String?
)

/**
 * Wraps Firebase Storage uploads for order files and user-owned images.
 */
@Singleton
class StorageRepository @Inject constructor(
    private val storage: FirebaseStorage,
    @ApplicationContext private val context: Context
) {
    companion object {
        /** Maximum allowed size for an order requirement/result file: 20 MB. */
        const val MAX_ORDER_FILE_BYTES = 20L * 1024 * 1024
    }

    suspend fun uploadProfilePhoto(userId: String, uri: Uri): Result<String> {
        return try {
            val rawName = uri.lastPathSegment?.substringAfterLast('/').orEmpty()
            val filename = if (rawName.isNotBlank()) rawName else "profile-${System.currentTimeMillis()}"
            val ref = storage.reference.child("users/$userId/profile/$filename")
            ref.putFile(uri).await()
            val url = ref.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadOrderRequirement(orderId: String, uri: Uri): Result<OrderFileUploadMetadata> {
        return uploadOrderFile(
            orderId = orderId,
            uri = uri,
            folder = "requirements",
            fallbackPrefix = "kebutuhan"
        )
    }

    suspend fun uploadOrderResult(orderId: String, uri: Uri): Result<OrderFileUploadMetadata> {
        return uploadOrderFile(
            orderId = orderId,
            uri = uri,
            folder = "result",
            fallbackPrefix = "hasil"
        )
    }

    private suspend fun uploadOrderFile(
        orderId: String,
        uri: Uri,
        folder: String,
        fallbackPrefix: String
    ): Result<OrderFileUploadMetadata> {
        return try {
            val size = fileSize(uri)

            if (size in 1..Long.MAX_VALUE && size > MAX_ORDER_FILE_BYTES) {
                return Result.failure(
                    IllegalArgumentException("Ukuran file maksimal 20 MB")
                )
            }

            val fileName = displayName(uri)
                ?: "$fallbackPrefix-${System.currentTimeMillis()}"
            val contentType = context.contentResolver.getType(uri)
            val storagePath = "orders/$orderId/$folder/$fileName"
            val ref = storage.reference.child(storagePath)
            ref.putFile(uri).await()
            Result.success(
                OrderFileUploadMetadata(
                    downloadUrl = ref.downloadUrl.await().toString(),
                    storagePath = storagePath,
                    fileName = fileName,
                    contentType = contentType
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadIdentityEvidence(
        userId: String,
        submissionId: String,
        uri: Uri
    ): Result<StorageUploadMetadata> {
        return uploadPrivateVerificationFile(
            userId = userId,
            submissionId = submissionId,
            uri = uri,
            kind = "identity",
            fallbackPrefix = "ktm"
        )
    }

    suspend fun uploadSkillEvidence(
        userId: String,
        submissionId: String,
        uri: Uri
    ): Result<StorageUploadMetadata> {
        return uploadPrivateVerificationFile(
            userId = userId,
            submissionId = submissionId,
            uri = uri,
            kind = "skill",
            fallbackPrefix = "bukti-keahlian"
        )
    }

    suspend fun uploadPortfolioImage(
        userId: String,
        portfolioItemId: String,
        uri: Uri
    ): Result<PublicImageUploadMetadata> {
        return uploadPublicUserImage(
            userId = userId,
            ownerPath = "portfolio/$portfolioItemId",
            uri = uri,
            fallbackPrefix = "portofolio",
            typeErrorMessage = "File portofolio harus berupa gambar"
        )
    }

    suspend fun uploadListingThumbnail(
        userId: String,
        listingId: String,
        uri: Uri
    ): Result<PublicImageUploadMetadata> {
        return uploadPublicUserImage(
            userId = userId,
            ownerPath = "listings/$listingId",
            uri = uri,
            fallbackPrefix = "listing",
            typeErrorMessage = "Gambar listing harus berupa file gambar"
        )
    }

    /**
     * Deletes a previously uploaded file by Storage path.
     *
     * The caller must only pass owner-owned paths that are allowed by
     * `storage.rules`.
     */
    suspend fun deleteFile(storagePath: String): Result<Unit> {
        return try {
            storage.reference.child(storagePath).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadPublicUserImage(
        userId: String,
        ownerPath: String,
        uri: Uri,
        fallbackPrefix: String,
        typeErrorMessage: String
    ): Result<PublicImageUploadMetadata> {
        return try {
            val size = fileSize(uri)
            if (size in 1..Long.MAX_VALUE && size > Validation.MAX_STORAGE_IMAGE_BYTES) {
                return Result.failure(
                    IllegalArgumentException("Ukuran gambar maksimal 5 MB")
                )
            }
            val contentType = context.contentResolver.getType(uri)
            if (contentType != null && !contentType.startsWith("image/")) {
                return Result.failure(
                    IllegalArgumentException(typeErrorMessage)
                )
            }
            val fileName = displayName(uri)
                ?: "$fallbackPrefix-${System.currentTimeMillis()}"
            val storagePath = "users/$userId/$ownerPath/$fileName"
            val ref = storage.reference.child(storagePath)
            ref.putFile(uri).await()
            Result.success(
                PublicImageUploadMetadata(
                    downloadUrl = ref.downloadUrl.await().toString(),
                    storagePath = storagePath,
                    fileName = fileName,
                    contentType = contentType
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadPrivateVerificationFile(
        userId: String,
        submissionId: String,
        uri: Uri,
        kind: String,
        fallbackPrefix: String
    ): Result<StorageUploadMetadata> {
        return try {
            val size = fileSize(uri)
            if (size in 1..Long.MAX_VALUE && size > Validation.MAX_VERIFICATION_FILE_BYTES) {
                return Result.failure(
                    IllegalArgumentException("Ukuran file maksimal 20 MB")
                )
            }
            val fileName = displayName(uri)
                ?: "$fallbackPrefix-${System.currentTimeMillis()}"
            val contentType = context.contentResolver.getType(uri)
            val storagePath = "users/$userId/verification/$kind/$submissionId/$fileName"
            storage.reference.child(storagePath).putFile(uri).await()
            Result.success(
                StorageUploadMetadata(
                    storagePath = storagePath,
                    fileName = fileName,
                    contentType = contentType
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun fileSize(uri: Uri): Long {
        return runCatching {
            context.contentResolver
                .openAssetFileDescriptor(uri, "r")
                ?.use { it.length }
                ?: -1L
        }.getOrDefault(-1L)
    }

    private fun displayName(uri: Uri): String? {
        val fromProvider = runCatching {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
            }
        }.getOrNull()

        return fromProvider
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: uri.lastPathSegment
                ?.substringAfterLast('/')
                ?.trim()
                ?.takeIf { it.isNotBlank() }
    }
}
