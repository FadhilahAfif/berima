# Architecture

## Tech Stack

| Layer | Choice |
|---|---|
| Language | Kotlin 2.2.10 |
| UI | Jetpack Compose (BOM 2026.02.01) |
| Architecture | MVVM |
| State | StateFlow + ViewModel |
| Navigation | Navigation Compose 2.9.5 (single activity) |
| DI | Hilt 2.59.2 (KSP) |
| Backend | Firebase (Auth, Firestore, Storage) — BOM 34.3.0 |
| Image loading | Coil 2.7.0 |
| Async | Kotlin Coroutines 1.9.0 |
| Build | AGP 9.2.1, Gradle 9.4.1, Java 11, minSdk 26, targetSdk 36 |

Google login is implemented with Android Credential Manager, Google ID tokens,
and Firebase Auth. Dependencies live in the version catalog; do not hardcode
versions in Gradle files. A Google-authenticated user gets a `users/{uid}`
profile document when one does not already exist.

## Package

Root package is `upnvj.berima.v1` (matches `applicationId` and `namespace`).

## Folder Structure

```
app/src/main/java/upnvj/berima/v1/
├── BerimaApplication.kt   # @HiltAndroidApp entry point
├── MainActivity.kt        # @AndroidEntryPoint, hosts BerimaApp()
├── data/
│   ├── model/             # User, Listing, Order, Review, Message, VerificationSubmission,
│   │                      # PortfolioItem, Constants
│   ├── repository/        # AuthRepository, ListingRepository, OrderRepository,
│   │                      # ReviewRepository, MessageRepository, StorageRepository,
│   │                      # VerificationRepository, PortfolioRepository
│   └── remote/            # Optional Firebase datasource wrappers (not created yet)
├── ui/
│   ├── BerimaApp.kt       # Root composable, owns NavController + Scaffold
│   ├── auth/              # LoginScreen, RegisterScreen + ViewModels (Phase 1 UI)
│   ├── home/              # HomeScreen, SearchScreen + ViewModels (Phase 2)
│   ├── listing/           # ListingDetail, CreateListing, EditListing (Phase 2)
│   ├── order/             # CreateOrder, Orders, OrderDetail (Phase 3)
│   ├── review/            # CreateReview (Phase 4)
│   ├── profile/           # Profile, EditProfile, UserProfile (Phase 4)
│   ├── verification/      # VerificationCenter, IdentityVerification, SkillVerification (PRD)
│   ├── portfolio/         # PortfolioManagerScreen + ViewModel (PRD P3)
│   ├── splash/            # SplashScreen (Phase 1 UI)
│   ├── common/            # Reusable composables (ListingCard, StatusChip, ...)
│   └── theme/             # BerimaTheme, Color, Type
├── navigation/
│   ├── NavGraph.kt        # NavHost, all destinations (placeholders until UI lands)
│   └── Screen.kt          # Sealed class with every route + createRoute helpers
└── di/
    └── AppModule.kt       # Provides FirebaseAuth, FirebaseFirestore, FirebaseStorage
```

## Version Catalog

All dependency versions live in `gradle/libs.versions.toml`. Never hardcode
versions in `build.gradle.kts`; always use `libs.xxx` aliases.

## Key Dependencies

```kotlin
// Compose
implementation(platform(libs.androidx.compose.bom))   // 2026.02.01
implementation(libs.androidx.compose.ui)
implementation(libs.androidx.compose.material3)
implementation(libs.androidx.compose.ui.tooling.preview)
implementation(libs.androidx.activity.compose)

// Lifecycle
implementation(libs.androidx.lifecycle.runtime.ktx)
implementation(libs.androidx.lifecycle.runtime.compose)
implementation(libs.androidx.lifecycle.viewmodel.compose)

// Navigation
implementation(libs.androidx.navigation.compose)       // 2.9.5

// Hilt
implementation(libs.hilt.android)                      // 2.59.2
ksp(libs.hilt.compiler)
implementation(libs.androidx.hilt.navigation.compose)  // 1.2.0

// Firebase
implementation(platform(libs.firebase.bom))            // 34.3.0
implementation(libs.firebase.auth)
implementation(libs.firebase.firestore)
implementation(libs.firebase.storage)

// Google login
implementation(libs.androidx.credentials)
implementation(libs.androidx.credentials.play.services.auth)
implementation(libs.googleid)

// Coil
implementation(libs.coil.compose)                      // 2.7.0

// Coroutines
implementation(libs.kotlinx.coroutines.android)        // 1.9.0
implementation(libs.kotlinx.coroutines.play.services)  // for .await()
```

## Plugins applied to `app`

```
com.android.application
org.jetbrains.kotlin.plugin.compose
com.google.dagger.hilt.android
com.google.devtools.ksp
com.google.gms.google-services
```

> **Note:** Do NOT apply `org.jetbrains.kotlin.android`. AGP 9 bundles
> built-in Kotlin support; applying `kotlin-android` on top throws
> "Cannot add extension with name 'kotlin'". AGP wires Kotlin in on its
> own when the Compose plugin is applied.

> **Note:** `gradle.properties` sets `android.disallowKotlinSourceSets=false`.
> KSP still registers its generated sources via `kotlin.sourceSets`, which
> AGP 9 blocks by default. Remove this flag once KSP ships a native
> AGP-9-compatible source registration path.

## Route Definitions (Screen.kt)

Canonical routes. Callers use `Screen.X.createRoute(...)` for routes with
arguments — never concatenate strings by hand.

```kotlin
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Orders : Screen("orders")
    data object Profile : Screen("profile")
    data object ListingDetail : Screen("listing/{listingId}") {
        const val ARG_LISTING_ID = "listingId"
        fun createRoute(listingId: String) = "listing/$listingId"
    }
    data object CreateListing : Screen("listing/create")
    data object EditListing : Screen("listing/edit/{listingId}") {
        const val ARG_LISTING_ID = "listingId"
        fun createRoute(listingId: String) = "listing/edit/$listingId"
    }
    data object CreateOrder : Screen("order/create/{listingId}") {
        const val ARG_LISTING_ID = "listingId"
        fun createRoute(listingId: String) = "order/create/$listingId"
    }
    data object OrderDetail : Screen("order/{orderId}") {
        const val ARG_ORDER_ID = "orderId"
        fun createRoute(orderId: String) = "order/$orderId"
    }
    data object CreateReview : Screen("review/create/{orderId}") {
        const val ARG_ORDER_ID = "orderId"
        fun createRoute(orderId: String) = "review/create/$orderId"
    }
    data object EditProfile : Screen("profile/edit")
    data object UserProfile : Screen("user/{userId}") {
        const val ARG_USER_ID = "userId"
        fun createRoute(userId: String) = "user/$userId"
    }
    data object VerificationCenter : Screen("verification")
    data object IdentityVerification : Screen("verification/identity")
    data object SkillVerification : Screen("verification/skill")
    data object PortfolioManager : Screen("portfolio")
}
```

## Firebase setup (one-time, manual)

1. Firebase Console → **Add project** → name it `Berima`.
2. Inside the project → **Add app** → Android.
3. Package name: `upnvj.berima.v1`, app nickname: `Berima`. SHA-1 is not needed
   for email/password auth; add later only if Google Sign-In is introduced.
4. Download `google-services.json` and drop it at `app/google-services.json`.
   The file is gitignored by `app/.gitignore` (verify before first commit).
5. In the Firebase console:
   - Authentication → enable **Email/Password**.
   - Authentication → enable **Google** before using Google login.
   - Create/configure a Web client ID for Google Sign-In and place it in
     `app/src/main/res/values/strings.xml` as `berima_web_client_id`. The
     checked-in fallback is `MISSING_WEB_CLIENT_ID` so local builds keep compiling
     before Firebase OAuth setup is finished.
   - Firestore Database → create in test mode for now. Security rules from
     `database.md` are deployed in Phase 5.
   - Storage → enable default bucket and deploy `storage.rules` before enabling
     KTM or skill-evidence uploads.
6. Build will fail with `File google-services.json is missing` until step 4 is
   done — this is expected.
