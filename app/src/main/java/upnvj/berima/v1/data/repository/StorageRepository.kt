package upnvj.berima.v1.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


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
            val size = runCatching {
                context.contentResolver
                    .openAssetFileDescriptor(uri, "r")
                    ?.use { it.length }
                    ?: -1L
            }.getOrDefault(-1L)

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
}
