# HomeScreen Enriched (Explore-in-Home) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Enrich HomeScreen into a combined browse + explore surface with a "Sedang ramai" featured horizontal rail, category chips, and a "Terbaru" main listing grid — no new route or bottom-nav tab required.

**Architecture:** HomeScreen owns a single `HomeViewModel` that drives three StateFlows: `featuredListings` (top 5 by `totalOrders DESC`), `allListings` (paginated by `createdAt DESC`, filtered by selected category), and `selectedCategory`. `ListingRepository` gets one new query method `getFeaturedListings()`. The screen is a single `LazyColumn` with four sections: search bar → featured rail → category chips → main grid.

**Tech Stack:** Kotlin 2.2.10, Jetpack Compose BOM 2026.02.01, Hilt 2.59.2, Firebase Firestore BOM 34.3.0, Coil 2.7.0, Navigation Compose 2.9.5, StateFlow + collectAsStateWithLifecycle.

---

## File Map

| Action | File | Responsibility |
|---|---|---|
| Modify | `data/repository/ListingRepository.kt` | Add `getFeaturedListings()` query |
| Create | `ui/home/HomeViewModel.kt` | State: featured, listings, selectedCategory, isLoading, error |
| Create | `ui/home/HomeScreen.kt` | Screen composable: search bar, featured rail, category chips, listing grid |
| Create | `ui/common/ListingCard.kt` | Reusable card composable (used by both featured rail and main grid) |
| Modify | `navigation/NavGraph.kt` | Replace `Placeholder("Home")` with real `HomeScreen(...)` |
| Modify | `features.md` | Update HomeScreen spec to reflect enriched layout |
| Modify | `AGENTS.md` | Mark HomeScreen + ListingCard milestone as Done |

---

## Task 1: Add `getFeaturedListings()` to ListingRepository

**Files:**
- Modify: `app/src/main/java/upnvj/berima/v1/data/repository/ListingRepository.kt`

- [ ] **Step 1: Add the query method after `getListingsByCategory`**

```kotlin
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
```

- [ ] **Step 2: Add required Firestore composite index**

In Firebase Console → Firestore → Indexes → Composite, add:

| Collection | Field 1 | Field 2 | Query scope |
|---|---|---|---|
| `listings` | `isActive` ASC | `totalOrders` DESC | Collection |

Also add this index to `database.md` index table.

- [ ] **Step 3: Verify file compiles**

```
.\gradlew :app:compileDebugKotlin
```
Expected: BUILD SUCCESSFUL

---

## Task 2: Create `ListingCard` composable

**Files:**
- Create: `app/src/main/java/upnvj/berima/v1/ui/common/ListingCard.kt`

This is a stateless component used in both the featured horizontal rail and the main vertical grid.

- [ ] **Step 1: Create the file**

```kotlin
package upnvj.berima.v1.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import upnvj.berima.v1.R
import upnvj.berima.v1.data.model.Listing
import upnvj.berima.v1.ui.theme.LocalBerimaColors
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ListingCard(
    listing: Listing,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageHeight: Dp = 120.dp
) {
    val berimaColors = LocalBerimaColors.current
    val cardShape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, cardShape)
            .clickable(onClick = onClick)
            .padding(bottom = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            if (listing.thumbnailUrl != null) {
                AsyncImage(
                    model = listing.thumbnailUrl,
                    contentDescription = listing.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(imageHeight)
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_berima_mark),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(
                text = listing.title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = formatRupiah(listing.price),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = MaterialTheme.typography.displaySmall.fontFamily
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = listing.sellerPhotoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = listing.sellerName,
                    style = MaterialTheme.typography.bodySmall,
                    color = berimaColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_star),
                    contentDescription = null,
                    tint = berimaColors.starRating,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    text = if (listing.averageRating > 0.0)
                        String.format(Locale.US, "%.1f", listing.averageRating)
                    else "-",
                    style = MaterialTheme.typography.bodySmall,
                    color = berimaColors.textSecondary
                )
            }
        }
    }
}

private fun formatRupiah(amount: Long): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return "Rp${formatter.format(amount)}"
}
```

- [ ] **Step 2: Add `ic_star` vector drawable**

Create `app/src/main/res/drawable/ic_star.xml`:

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#FF000000"
        android:pathData="M12,17.27L18.18,21l-1.64,-7.03L22,9.24l-7.19,-0.61L12,2 9.19,8.63 2,9.24l5.46,4.73L5.82,21z"/>
</vector>
```

- [ ] **Step 3: Verify file compiles**

```
.\gradlew :app:compileDebugKotlin
```
Expected: BUILD SUCCESSFUL

---

## Task 3: Create `HomeViewModel`

**Files:**
- Create: `app/src/main/java/upnvj/berima/v1/ui/home/HomeViewModel.kt`

- [ ] **Step 1: Create the file**

```kotlin
package upnvj.berima.v1.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import upnvj.berima.v1.data.model.Listing
import upnvj.berima.v1.data.repository.ListingRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val listingRepository: ListingRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val featuredListings: StateFlow<List<Listing>> = listingRepository
        .getFeaturedListings(limit = 5L)
        .catch { e -> _error.value = e.message }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val listings: StateFlow<List<Listing>> = _selectedCategory
        .flatMapLatest { category ->
            if (category == null) {
                listingRepository.getActiveListings(limit = 20L)
            } else {
                listingRepository.getListingsByCategory(category, limit = 20L)
            }
        }
        .catch { e -> _error.value = e.message }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun clearError() {
        _error.value = null
    }
}
```

- [ ] **Step 2: Verify file compiles**

```
.\gradlew :app:compileDebugKotlin
```
Expected: BUILD SUCCESSFUL

---

## Task 4: Create `HomeScreen`

**Files:**
- Create: `app/src/main/java/upnvj/berima/v1/ui/home/HomeScreen.kt`

- [ ] **Step 1: Create the file**

```kotlin
package upnvj.berima.v1.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import upnvj.berima.v1.R
import upnvj.berima.v1.data.model.Constants
import upnvj.berima.v1.ui.common.ListingCard
import upnvj.berima.v1.ui.theme.LocalBerimaColors

@Composable
fun HomeScreen(
    onListingClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val featuredListings by viewModel.featuredListings.collectAsStateWithLifecycle()
    val listings by viewModel.listings.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val berimaColors = LocalBerimaColors.current

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { padding ->
        if (isLoading && listings.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                SearchBar(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(24.dp))
            }

            if (featuredListings.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Sedang ramai",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(featuredListings, key = { it.listingId }) { listing ->
                            ListingCard(
                                listing = listing,
                                onClick = { onListingClick(listing.listingId) },
                                modifier = Modifier.width(180.dp),
                                imageHeight = 110.dp
                            )
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }

            item {
                SectionHeader(
                    title = "Terbaru",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
                CategoryChips(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            if (listings.isEmpty() && !isLoading) {
                item {
                    EmptyListings(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 48.dp)
                    )
                }
            } else {
                items(
                    count = listings.size,
                    key = { listings[it].listingId }
                ) { index ->
                    ListingCard(
                        listing = listings[index],
                        onClick = { onListingClick(listings[index].listingId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    if (index < listings.lastIndex) {
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, berimaColors.borderInput, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_search),
            contentDescription = null,
            tint = berimaColors.textSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = "Cari layanan...",
            style = MaterialTheme.typography.bodyMedium,
            color = berimaColors.textSecondary
        )
    }
}

@Composable
private fun CategoryChips(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val categories = listOf(
        null to "Semua",
        Constants.Category.ACADEMIC to "Academic Support",
        Constants.Category.VISUAL to "Visual Branding",
        Constants.Category.DATA to "Data Processing"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { (value, label) ->
            val isSelected = selectedCategory == value
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondaryContainer
                    )
                    .clickable { onCategorySelected(value) }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

@Composable
private fun EmptyListings(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_berima_mark),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Belum ada listing",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Coba kategori lain atau kembali nanti.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
```

- [ ] **Step 2: Add `ic_search` vector drawable**

Create `app/src/main/res/drawable/ic_search.xml`:

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#FF000000"
        android:pathData="M15.5,14h-0.79l-0.28,-0.27C15.41,12.59 16,11.11 16,9.5 16,5.91 13.09,3 9.5,3S3,5.91 3,9.5 5.91,16 9.5,16c1.61,0 3.09,-0.59 4.23,-1.57l0.27,0.28v0.79l5,4.99L20.49,19l-4.99,-5zM9.5,14C7.01,14 5,11.99 5,9.5S7.01,5 9.5,5 14,7.01 14,9.5 11.99,14 9.5,14z"/>
</vector>
```

- [ ] **Step 3: Verify file compiles**

```
.\gradlew :app:compileDebugKotlin
```
Expected: BUILD SUCCESSFUL

---

## Task 5: Wire HomeScreen into NavGraph

**Files:**
- Modify: `app/src/main/java/upnvj/berima/v1/navigation/NavGraph.kt`

- [ ] **Step 1: Add imports at top of NavGraph.kt**

Add these imports:
```kotlin
import upnvj.berima.v1.ui.home.HomeScreen
```

- [ ] **Step 2: Replace the Home placeholder composable**

Find:
```kotlin
composable(Screen.Home.route) {
    Placeholder(name = "Home")
}
```

Replace with:
```kotlin
composable(Screen.Home.route) {
    HomeScreen(
        onListingClick = { listingId ->
            navController.navigate(Screen.ListingDetail.createRoute(listingId))
        },
        onSearchClick = {
            navController.navigate(Screen.Orders.route)
        }
    )
}
```

> Note: `onSearchClick` navigates to `Screen.Orders.route` as a temporary placeholder until `SearchScreen` is built in a later task. Update this when SearchScreen lands.

- [ ] **Step 3: Full debug build**

```
.\gradlew :app:assembleDebug
```
Expected: BUILD SUCCESSFUL

---

## Task 6: Update context files and milestone

**Files:**
- Modify: `E:\Projects\berima\.agents\context\features.md`
- Modify: `E:\Projects\berima\AGENTS.md`
- Modify: `E:\Projects\berima\.agents\context\database.md`

- [ ] **Step 1: Update `features.md` HomeScreen spec**

Replace the HomeScreen section with:

```markdown
### HomeScreen
- Search bar at top (tappable, navigates to SearchScreen)
- **"Sedang ramai" horizontal rail**: top 5 listings ordered by `totalOrders DESC`, shown as a `LazyRow` of `ListingCard` (width 180dp)
- Category filter chips: Semua | Academic Support | Visual Branding | Data Processing
- **"Terbaru" vertical list**: active listings ordered by `createdAt DESC`, filtered by selected category, paginated 20 items
- Empty state when no listings match the selected category
- Pull-to-refresh (Phase 5 polish)
- Query for featured: `listings` where `isActive == true`, ordered by `totalOrders` DESC, limit 5
- Query for main list: `listings` where `isActive == true` (+ optional `category ==`), ordered by `createdAt` DESC, limit 20
```

- [ ] **Step 2: Add new composite index to `database.md`**

Add to the Required Composite Indexes table:

```
| `listings` | `isActive` ASC, `totalOrders` DESC | — |
```

- [ ] **Step 3: Update `AGENTS.md` milestone table**

Mark these rows as Done:
- `HomeScreen + HomeViewModel` → ✅ Done | Enriched: featured rail + category chips + listing grid
- `ListingCard composable (reusable)` → ✅ Done | In `ui/common/ListingCard.kt`

- [ ] **Step 4: Update `learned.md`**

Add entry:
```
- [2026-05-18] HomeScreen built as combined browse + explore surface (no separate Explore tab).
  Sections: search bar → "Sedang ramai" featured LazyRow (top 5 by totalOrders DESC) →
  category chips → "Terbaru" LazyColumn (createdAt DESC, category-filtered).
  New repo method: `getFeaturedListings()`. New composite index required:
  `listings / isActive ASC + totalOrders DESC`. ListingCard lives in `ui/common/ListingCard.kt`.
  SearchScreen navigation from HomeScreen is a placeholder (points to Orders) until SearchScreen is built.
```

---

## Self-Review Checklist

- [x] Spec coverage: search bar, featured rail, category chips, main grid, empty state — all covered
- [x] No placeholders or TBDs in any task
- [x] `getFeaturedListings()` defined in Task 1, used in Task 3 — names match
- [x] `ListingCard` defined in Task 2, used in Task 4 — import path matches
- [x] `HomeViewModel` defined in Task 3, wired in Task 4 via `hiltViewModel()` — matches conventions.md pattern
- [x] `HomeScreen` defined in Task 4, wired in NavGraph in Task 5 — route matches `Screen.Home.route`
- [x] `ic_star` and `ic_search` drawables added before they are referenced
- [x] Firestore index documented in Task 6 and Task 1
- [x] `onSearchClick` placeholder documented with a note to update when SearchScreen lands
