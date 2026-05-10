# Architecture

## Tech Stack

| Layer | Choice |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM |
| State | StateFlow + ViewModel |
| Navigation | Navigation Compose (single activity) |
| DI | Hilt |
| Backend | Firebase (Auth, Firestore, Storage) |
| Image loading | Coil |
| Async | Kotlin Coroutines |

## Folder Structure

```
app/src/main/java/com/berima/app/
├── data/
│   ├── model/              # Data classes: User, Listing, Order, Review, Message
│   ├── repository/         # One repository per domain: AuthRepository, ListingRepository,
│   │                       # OrderRepository, ReviewRepository, MessageRepository
│   └── remote/             # Firebase datasource wrappers (if needed)
├── ui/
│   ├── auth/               # LoginScreen, RegisterScreen + their ViewModels
│   ├── home/               # HomeScreen, SearchScreen + ViewModels
│   ├── listing/            # ListingDetailScreen, CreateListingScreen, EditListingScreen
│   ├── order/              # CreateOrderScreen, OrdersScreen, OrderDetailScreen
│   ├── review/             # CreateReviewScreen
│   ├── profile/            # ProfileScreen, EditProfileScreen, UserProfileScreen
│   ├── splash/             # SplashScreen
│   └── common/             # Reusable composables (ListingCard, UserAvatar, StatusChip, etc.)
├── navigation/
│   ├── NavGraph.kt         # All NavHost destinations defined here
│   └── Screen.kt           # Sealed class with all route strings
└── di/
    └── AppModule.kt        # Hilt module providing Firebase instances
```

## Key Dependencies (build.gradle.kts)

```kotlin
// Compose BOM
implementation(platform("androidx.compose:compose-bom:2024.02.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui-tooling-preview")
implementation("androidx.activity:activity-compose:1.8.2")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

// Hilt
implementation("com.google.dagger:hilt-android:2.51")
kapt("com.google.dagger:hilt-compiler:2.51")
implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

// Firebase
implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-storage-ktx")

// Coil
implementation("io.coil-kt:coil-compose:2.6.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

## Route Definitions (Screen.kt)

```kotlin
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Orders : Screen("orders")
    object Profile : Screen("profile")
    object ListingDetail : Screen("listing/{listingId}") {
        fun createRoute(listingId: String) = "listing/$listingId"
    }
    object CreateListing : Screen("listing/create")
    object EditListing : Screen("listing/edit/{listingId}") {
        fun createRoute(listingId: String) = "listing/edit/$listingId"
    }
    object CreateOrder : Screen("order/create/{listingId}") {
        fun createRoute(listingId: String) = "order/create/$listingId"
    }
    object OrderDetail : Screen("order/{orderId}") {
        fun createRoute(orderId: String) = "order/$orderId"
    }
    object CreateReview : Screen("review/create/{orderId}") {
        fun createRoute(orderId: String) = "review/create/$orderId"
    }
    object EditProfile : Screen("profile/edit")
    object UserProfile : Screen("user/{userId}") {
        fun createRoute(userId: String) = "user/$userId"
    }
}
```
