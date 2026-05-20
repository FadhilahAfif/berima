# Learned

Document discoveries here as the project progresses.
Each entry must include: what was learned, which file it affects (if any), and date.


- [2026-05-11] Package is `upnvj.berima.v1`, not `com.berima.app` as originally
  drafted in architecture.md. All code, applicationId, and namespace standardized
  on `upnvj.berima.v1`. → Updated architecture.md.
- [2026-05-11] Upgraded the stack to match what was already on disk: Kotlin 2.2.10,
  Compose BOM 2026.02.01, AGP 9.2.1, Gradle 9.4.1, Java 11. Picked latest compatible
  versions for the rest: Hilt 2.59.2, Firebase BOM 34.3.0, Navigation Compose 2.9.5,
  Coil 2.7.0, Coroutines 1.9.0, KSP 2.2.10-2.0.2. → Updated architecture.md.
- [2026-05-11] Using KSP instead of kapt for Hilt. Faster, first-class on Kotlin 2.x,
  and supported by Dagger 2.59+. → Noted in architecture.md.
- [2026-05-11] `google-services.json` is NOT committed. App build will fail until it
  is placed at `app/google-services.json`. Manual Firebase setup checklist added to
  `architecture.md` → "Firebase setup (one-time, manual)".
- [2026-05-11] Added `kotlinx-coroutines-play-services` dependency. Needed for
  `Task<T>.await()` used across every repository. Not mentioned in the original
  architecture.md. → Added to architecture.md.
- [2026-05-11] Navigation arguments keys are exposed as `const val ARG_*` on each
  `Screen` object (e.g. `Screen.ListingDetail.ARG_LISTING_ID`). Keeps route
  placeholder strings and `backStackEntry.arguments.getString(...)` lookups in sync.
- [2026-05-11] `ReviewRepository.createReview` uses a Firestore transaction to write
  the review + bump listing and seller rating aggregates atomically. We also write a
  new `reviewCount` field on the listing (not in the original schema) since
  `totalOrders` is the wrong divisor for an average rating. → Note: add `reviewCount`
  to Listing schema in `database.md` if/when the schema is re-synced.
- [2026-05-11] AGP 9.2.1 applies the Kotlin plugin itself. Applying
  `org.jetbrains.kotlin.android` on top throws
  "Cannot add extension with name 'kotlin'". Only the Compose plugin
  (`org.jetbrains.kotlin.plugin.compose`) is needed in the app module; AGP
  pulls in `kotlin-android` transparently. → Noted in architecture.md.
- [2026-05-11] AGP 9 disallows `kotlin.sourceSets` by default, which breaks KSP
  (KSP registers its generated `build/generated/ksp/.../kotlin` directory that way).
  Added `android.disallowKotlinSourceSets=false` to `gradle.properties`. Remove
  once KSP + AGP 9 ship a native accommodation.
- [2026-05-11] Firebase BOM 34 no longer publishes `-ktx` artifacts
  (`firebase-auth-ktx`, `firebase-firestore-ktx`, `firebase-storage-ktx`).
  The Kotlin extensions now live in the main modules. Use `firebase-auth`,
  `firebase-firestore`, `firebase-storage`. The top-level accessors moved too:
  `com.google.firebase.Firebase` (not `.ktx.Firebase`) and e.g.
  `com.google.firebase.firestore.firestore` (not `.ktx.firestore`).
  → Updated architecture.md + AppModule.kt.
- [2026-05-11] Verified the full build chain with a throwaway stub
  `google-services.json` placed at `app/google-services.json`. `assembleDebug`
  succeeded end-to-end: KSP → Hilt codegen → Kotlin compile → dex → APK. Stub
  file was removed after verification, and `app/.gitignore` now ignores
  `google-services.json` so real files can never be accidentally committed.
- [2026-05-11] Real `google-services.json` placed at `app/google-services.json`.
  Verified `assembleDebug` produces a real APK. Firebase wiring is live.
  Project: `berima-74938`, package `upnvj.berima.v1`.
- [2026-05-11] Gradle Daemon JVM Provisioning picks up the first "JDK 21" it
  finds on disk, which on a machine with VSCode + Red Hat Java extension is
  `.vscode\extensions\redhat.java-*\jre\21.0.10-*`. That's a JRE, not a JDK,
  so `jlink.exe` is missing and the build aborts with
  "jlink executable ... does not exist". Fixed by:
  (1) deleting the auto-generated `gradle/gradle-daemon-jvm.properties`,
  (2) adding `org.gradle.java.installations.auto-detect=false` to
  `gradle.properties` so Gradle uses only the JVM that launched it.
  Requires setting `JAVA_HOME` to a real JDK (Android Studio JBR at
  `C:\Program Files\Android\Android Studio\jbr` works).
- [2026-05-17] Design system fully reconciled into DESIGN.MD. All tokens are
  single source of truth. Berima-only tokens (primary-dim, surface-pressed,
  border-subtle, border-input, surface-raised, container-green, star-rating,
  all status.*) live on `BerimaColors` data class via `LocalBerimaColors`
  CompositionLocal. M3 ColorScheme slots filled from the same token set.
- [2026-05-17] Plus Jakarta Sans bundled as 4 static TTF files in
  `app/src/main/res/font/` (regular/semibold/bold/extrabold, weights
  400/600/700/800). Downloaded from fonts.gstatic.com v12. Do NOT switch to
  Downloadable Fonts API — offline availability is required.
- [2026-05-17] `BerimaTheme` is light-only (`darkTheme = false`,
  `dynamicColor = false`). Dark mode deferred. Dynamic color disabled so
  Android 12+ wallpaper extraction cannot override the brand palette.
- [2026-05-17] Shared UI components (`BerimaButton`, `BerimaTextField`) live
  in `ui/common/Components.kt`. Import from there — do not redefine per screen.
- [2026-05-17] Password visibility toggle icons added as vector drawables:
  `ic_visibility.xml` and `ic_visibility_off.xml` in `res/drawable/`.
- [2026-05-17] `Typography` val renamed to `BerimaTypography` in `Type.kt` to
  avoid shadowing the M3 default. `Theme.kt` references `BerimaTypography`.
- [2026-05-17] Phase 1 UI (Splash, Login, Register) verified: `assembleDebug`
  BUILD SUCCESSFUL in 35s with KSP + Hilt codegen + Compose compile all clean.
- [2026-05-17] Removed campus email restriction. `Validation.ALLOWED_EMAIL_DOMAIN` and
  `isAllowedEmail()` replaced with `isValidEmail()` using `android.util.Patterns.EMAIL_ADDRESS`.
  All UPNVJ-specific strings removed from LoginScreen, RegisterScreen, RegisterViewModel,
  and AuthRepository. App now accepts any valid email address.
- [2026-05-17] Auth + Splash visual upgrade. `BerimaTextField` gained a
  `leadingIcon` parameter (mirrors `trailingIcon`) so any field can show a
  leading symbol without ad-hoc Row wrappers. New vector drawables added in
  `app/src/main/res/drawable/`: `ic_mail.xml`, `ic_lock.xml`, `ic_person.xml`.
  SplashScreen now stacks `ic_berima_mark` on a primaryContainer halo above
  `ic_berima_wordmark`, with a 600ms-delayed pulsing-dots loader plus
  `BERIMA · 2026` label anchored at the bottom. Login/Register screens use a
  brand-mark chip + 2-line `displayLarge` headline ("Halo,\nselamat datang." /
  "Buat\nakun baru.") and a single-line annotated footer link with a chevron
  (replaces the old `TextButton` row). Both screens stagger entry animations
  (hero → fields → button → footer) using `Animatable` + `tween`.
- [2026-05-17] Convention: when comparing UI directions, encode each variant
  as a `*Content` private composable inside the same screen file plus a named
  `@Preview` per variant (e.g. "Login · A · Editorial",
  "Login · B · Bottom sheet"). Keeps draft variants out of the runtime
  navigation graph and discoverable via Android Studio's preview pane.
  Once a winner is picked, delete the loser's `*ContentSheet` and preview.
- [2026-05-17] Auth visual direction picked: **Editorial-grounded (Direction A)**.
  Bottom-sheet panel variant (Direction B) was tried as a side-by-side preview
  and rejected — the cream-canvas + tonal layering reads stronger without a
  panel split. Direction B drafts removed from `LoginScreen.kt` and
  `RegisterScreen.kt`; only the editorial layout remains.
- [2026-05-18] Every screen must have a `@Preview` composable at the bottom of its file.
  Follow the pattern from SplashScreen/LoginScreen: detect `LocalInspectionMode.current`
  to seed Animatable with final values and skip LaunchedEffect blocks.
  For screens with ViewModels, create a preview-safe wrapper that passes fake/default data
  directly instead of using `hiltViewModel()`. Never call `hiltViewModel()` inside a `@Preview`.
- [2026-05-18] Firebase CLI installed globally via npm (`firebase-tools`). User is logged in.
  Use `firebase deploy --only firestore:rules` and `firebase deploy --only firestore:indexes`
  in Phase 5 instead of manual console steps.
- [2026-05-18] All 8 Firestore composite indexes created manually in Firebase Console.
  Future indexes should be added to `database.md` and deployed via Firebase CLI in Phase 5.
- [2026-05-18] HomeScreen built as combined browse + explore surface (no separate Explore tab).
  Sections: search bar → "Sedang ramai" featured LazyRow (top 5 by totalOrders DESC) →
  category chips → "Terbaru" LazyColumn (createdAt DESC, category-filtered, limit 20).
  New repo method: `getFeaturedListings()`. New composite index required:
  `listings / isActive ASC + totalOrders DESC`. ListingCard lives in `ui/common/ListingCard.kt`.
  onSearchClick in NavGraph is a placeholder (navigates to Orders) until SearchScreen is built.
- [2026-05-17] Compose Preview gotcha: `LaunchedEffect` blocks do NOT run in
  Android Studio's `@Preview` renderer. Any composable that uses
  `Animatable(0f)` + `LaunchedEffect { animateTo(1f) }` for an entry
  animation will render with alpha 0 / offset values stuck at their initial
  state, producing a blank preview while runtime works fine. Fix: detect
  preview via `LocalInspectionMode.current` and seed `Animatable(...)` with
  the *final* value when in preview, plus skip the `LaunchedEffect` block
  entirely. Example pattern lives in `SplashScreen.kt`, `LoginScreen.kt`,
  `RegisterScreen.kt`. Apply this to every future screen with entry
  animations.
- [2026-05-20] Phase 3 counter semantics: `listings.totalOrders`,
  `users.totalOrdersAsBuyer`, `users.totalOrdersAsSeller` are bumped only
  when an order transitions to `paid`, NOT on `createOrder`. Bumping on
  pending inflates Home's "Sedang ramai" rail with un-transacted demand and
  forces decrement logic for cancellations. The bump now happens inside
  `OrderRepository.markPaid()` as a single Firestore transaction so the
  status flip + 3 counter increments either all land or none do. Method is
  idempotent: re-entering when status is already `paid` short-circuits with
  no writes — defends against double-tap on "Simulasi Bayar".
- [2026-05-20] Two-ViewModel pattern on `OrderDetailScreen`:
  `OrderDetailViewModel` owns the order doc + action dispatch,
  `ChatViewModel` owns messages + send. Both injected via `hiltViewModel()`
  on the same screen. Both read `orderId` from `SavedStateHandle` keyed on
  `Screen.OrderDetail.ARG_ORDER_ID` since `hiltViewModel()` populates the
  handle from nav args. Different lifecycles, separate error channels —
  better than a fat single VM.
- [2026-05-20] Action dispatch on order detail uses a sealed
  `OrderAction` interface (in `ui/order/OrderAction.kt`) routed through one
  `onAction(action: OrderAction)` entry point. The composable decides which
  buttons to render based on `(order.status, isBuyer)`; the ViewModel owns
  the role × status execution. Keeps the matrix in one place.
- [2026-05-20] `StorageRepository` (new in Phase 3) wraps Firebase Storage
  uploads. Order results go to `orders/{orderId}/result/{filename}` with a
  20 MB pre-flight size check via
  `contentResolver.openAssetFileDescriptor(uri, "r").length`. Some content
  providers return -1 for length; we treat unknown-size as allow and rely on
  Firebase's own limits as a backstop. Production storage rules must
  restrict reads/writes on `/orders/{orderId}/result/**` to the buyer and
  seller of that order — not yet deployed (Phase 5 task).
- [2026-05-20] Material3 1.3+ (Compose BOM 2026.02.01) reshaped `TabRow`'s
  indicator API: legacy `indicator: @Composable (List<TabPosition>) -> Unit`
  is replaced with `TabIndicatorScope.() -> Unit` and the old
  `TabRowDefaults.tabIndicatorOffset(TabPosition)` is deprecated. For
  simple two-tab layouts use `PrimaryTabRow` which gives the spec's
  primary-color underline by default — no custom indicator slot needed.
  Use `divider = {}` to remove the default `HorizontalDivider` so the row
  sits clean on the cream canvas. See `OrdersScreen.kt` for the pattern.
- [2026-05-20] `material-icons-extended` is NOT in the project. For send
  buttons and similar one-off needs use a Unicode glyph in a `Text`
  composable (e.g. `"\u27A4"` for ➤) inside a clickable `Box` styled as a
  pill. `IconButton` enforces a fixed 40dp internal size that breaks brand
  pill sizing — use `Box(...).clickable` instead.
