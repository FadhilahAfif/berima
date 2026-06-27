# Coding Conventions

## Naming

| Element | Convention | Example |
|---|---|---|
| Class / Object | PascalCase | `ListingRepository`, `OrderViewModel` |
| Function / Variable | camelCase | `createOrder()`, `currentUser` |
| Constant | SCREAMING_SNAKE_CASE | `MAX_DESCRIPTION_LENGTH` |
| File | Same as primary class | `ListingRepository.kt` |
| Composable (screen) | PascalCase + `Screen` suffix | `HomeScreen`, `OrderDetailScreen` |
| Composable (component) | PascalCase, no suffix | `ListingCard`, `StatusChip`, `UserAvatar` |
| Firestore field names | camelCase | `createdAt`, `sellerId`, `averageRating` |

---

## Data Classes

All Firestore model classes must:
- Be `data class`
- Have default values for all fields (required for Firestore deserialization)
- Use `@DocumentId` on the ID field

```kotlin
data class Listing(
    @DocumentId val listingId: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val price: Long = 0L,
    val deliveryTimeHours: Int = 24,
    val isActive: Boolean = true,
    val averageRating: Double = 0.0,
    val totalOrders: Int = 0,
    val createdAt: Timestamp = Timestamp.now()
)
```

---

## ViewModel Pattern

Every ViewModel must follow this structure exactly:

```kotlin
@HiltViewModel
class ListingViewModel @Inject constructor(
    private val listingRepository: ListingRepository
) : ViewModel() {

    // Private mutable, public immutable
    private val _listings = MutableStateFlow<List<Listing>>(emptyList())
    val listings: StateFlow<List<Listing>> = _listings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadListings()
    }

    fun loadListings() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                listingRepository.getActiveListings().collect { result ->
                    _listings.value = result
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
```

---

## Repository Pattern

Repositories must:
- Never expose Firebase types to ViewModel
- Return `Flow<T>` for stream data, `Result<T>` for one-shot operations
- Handle all exceptions internally

```kotlin
class ListingRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getActiveListings(): Flow<List<Listing>> = callbackFlow {
        val listener = firestore.collection("listings")
            .whereEqualTo("isActive", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val listings = snapshot?.documents
                    ?.mapNotNull { it.toObject(Listing::class.java) }
                    ?: emptyList()
                trySend(listings)
            }
        awaitClose { listener.remove() }
    }

    suspend fun createListing(listing: Listing): Result<String> {
        return try {
            val ref = firestore.collection("listings").document()
            ref.set(listing.copy(listingId = ref.id)).await()
            Result.success(ref.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

## Composable Pattern

Screen composables: receive ViewModel via `hiltViewModel()`, never pass ViewModel down.
Component composables: stateless, receive data and lambdas only.

```kotlin
// Screen — owns ViewModel, handles navigation
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onListingClick: (String) -> Unit
) {
    val listings by viewModel.listings.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        // content
    }
}

// Component — stateless, always has modifier parameter
@Composable
fun ListingCard(
    listing: Listing,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // UI only, no business logic
}
```

---

## Rules

1. **Use `collectAsStateWithLifecycle()`** — never `collectAsState()`
2. **Single Activity** — all screens via Navigation Compose, no multiple activities
3. **No LiveData** — use StateFlow only
4. **No direct Firestore access from Composable** — always go through ViewModel → Repository
5. **Always handle loading and error state** in every ViewModel
6. **Use `Result<T>`** for repository functions that can fail
7. **Always add `modifier: Modifier = Modifier`** parameter to every composable
8. **No hardcoded strings** in composables — use string resources or constants
9. **Every PR must compile** — do not merge broken code
10. **One screen per PR** — do not mix multiple screens in one commit
11. **No public identity document URLs** — KTM and verification evidence use private Storage paths, not public profile/listing fields
12. **Protect admin-managed fields** — users must never be able to update verification approval status or public badge fields from the client
13. **PRD requirements need traceability** — when implementing PRD work, mention the relevant requirement IDs in code review notes, PR descriptions, or task summaries
14. **Google auth is profile-compatible** — if a Google-authenticated user has no `users/{uid}` document, create the same default profile shape used by email registration
