package upnvj.berima.v1.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import upnvj.berima.v1.data.model.User
import upnvj.berima.v1.data.model.UserRole
import upnvj.berima.v1.data.model.Validation
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles Firebase Auth (email + password) and the matching
 * `users/{uid}` profile document.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val usersCollection = firestore.collection("users")

    val currentUserId: String?
        get() = auth.currentUser?.uid

    fun isLoggedIn(): Boolean = auth.currentUser != null

    /**
     * Emits the current auth user's UID (or null if signed out) whenever
     * Firebase Auth state changes.
     */
    fun observeAuthState(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun signIn(email: String, password: String): Result<String> {
        if (!Validation.isValidEmail(email)) {
            return Result.failure(
                IllegalArgumentException("Format email tidak valid")
            )
        }
        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val uid = result.user?.uid
                ?: return Result.failure(IllegalStateException("UID tidak tersedia"))
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<String> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user
                ?: return Result.failure(IllegalStateException("Akun Google tidak tersedia"))
            val uid = firebaseUser.uid
            val userRef = usersCollection.document(uid)
            val existingProfile = userRef.get().await()

            if (!existingProfile.exists()) {
                val email = firebaseUser.email.orEmpty().trim().lowercase()
                val fallbackName = email.substringBefore("@").ifBlank { "Pengguna Berima" }
                val user = User(
                    uid = uid,
                    name = firebaseUser.displayName?.trim().takeUnless { it.isNullOrBlank() }
                        ?: fallbackName,
                    email = email,
                    photoUrl = firebaseUser.photoUrl?.toString(),
                    role = UserRole.BOTH,
                    createdAt = Timestamp.now()
                )
                userRef.set(user).await()
            }

            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        if (!Validation.isValidEmail(email)) {
            return Result.failure(
                IllegalArgumentException("Masukkan email yang valid terlebih dahulu")
            )
        }
        return try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Creates the Auth account, then writes the matching profile document.
     * If the profile write fails, the Auth account is rolled back so the
     * user can retry.
     */
    suspend fun signUp(name: String, email: String, password: String): Result<String> {
        if (!Validation.isValidEmail(email)) {
            return Result.failure(
                IllegalArgumentException("Format email tidak valid")
            )
        }
        if (password.length < Validation.MIN_PASSWORD_LENGTH) {
            return Result.failure(
                IllegalArgumentException(
                    "Password minimal ${Validation.MIN_PASSWORD_LENGTH} karakter"
                )
            )
        }
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("Nama tidak boleh kosong"))
        }

        return try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val uid = result.user?.uid
                ?: return Result.failure(IllegalStateException("UID tidak tersedia"))

            val user = User(
                uid = uid,
                name = name.trim(),
                email = email.trim().lowercase(),
                role = UserRole.BOTH,
                createdAt = Timestamp.now()
            )

            try {
                usersCollection.document(uid).set(user).await()
                Result.success(uid)
            } catch (e: Exception) {
                // Roll back Auth user so the account isn't orphaned.
                runCatching { auth.currentUser?.delete()?.await() }
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    /** Stream the profile document for a specific user. */
    fun observeUser(uid: String): Flow<User?> = callbackFlow {
        val listener = usersCollection.document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(User::class.java))
            }
        awaitClose { listener.remove() }
    }

    suspend fun getUser(uid: String): Result<User?> {
        return try {
            val snapshot = usersCollection.document(uid).get().await()
            Result.success(snapshot.toObject(User::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(
        uid: String,
        name: String,
        bio: String?,
        faculty: String?,
        role: String,
        photoUrl: String?
    ): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any?>(
                "name" to name,
                "bio" to bio,
                "faculty" to faculty,
                "role" to role
            )
            if (photoUrl != null) {
                updates["photoUrl"] = photoUrl
            }
            usersCollection.document(uid).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
