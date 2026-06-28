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

/**
 * Wraps Firebase Storage uploads for order result files and user profile photos.
 */
@Singleton
class StorageRepository @Inject constructor(
    private val storage: FirebaseStorage,
    @ApplicationContext private val context: Context
) {
    companion object {
        /** Maximum allowed size for an order result file: 20 MB. */
        const val MAX_RESULT_FILE_BYTES = 20L * 1024 * 1024
    }

    /**
     * Uploads the file at [uri] to `orders/{orderId}/result/{filename}` and
     * returns the public download URL. Rejects files larger than
     * [MAX_RESULT_FILE_BYTES] before the upload starts.
     *
     * Some content providers don't expose a known length (returns -1). In
     * that case we trust Firebase's own limits as a backstop.
     */
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

    suspend fun uploadOrderResult(orderId: String, uri: Uri): Result<String> {
        return try {
            val size = fileSize(uri)

            if (size in 1..Long.MAX_VALUE && size > MAX_RESULT_FILE_BYTES) {
                return Result.failure(
                    IllegalArgumentException("Ukuran file maksimal 20 MB")
                )
            }

            val rawName = uri.lastPathSegment?.substringAfterLast('/').orEmpty()
            val filename = if (rawName.isNotBlank()) rawName else "result-${System.currentTimeMillis()}"
            val ref = storage.reference.child("orders/$orderId/result/$filename")
            ref.putFile(uri).await()
            val url = ref.downloadUrl.await().toString()
            Result.success(url)
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
                    IllegalArgumentException("File portofolio harus berupa gambar")
                )
            }
            val fileName = displayName(uri)
                ?: "portofolio-${System.currentTimeMillis()}"
            val storagePath = "users/$userId/portfolio/$portfolioItemId/$fileName"
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

    suspend fun deleteFile(storagePath: String): Result<Unit> {
        return try {
            storage.reference.child(storagePath).delete().await()
            Result.success(Unit)
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
