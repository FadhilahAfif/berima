# Learned

Document discoveries here as the project progresses.
Each entry must include: what was learned, which file it affects (if any), and date.

- [2026-06-28] Phase 10 PRD P3 portfolio and badge work shipped. Portfolio CRUD
  now uses a dedicated `PortfolioRepository` and one Profile-owned
  `PortfolioManagerScreen` for create/edit/delete, with optional image upload to
  `users/{uid}/portfolio/{portfolioItemId}/{filename}` and public download URLs
  stored only on portfolio items. `ProfileScreen` shows a portfolio preview and
  "Kelola" entry, while `UserProfileScreen` displays public portfolio items and
  opens external links through Compose `LocalUriHandler`. Badge UI is centralized
  in `VerificationBadgeRow`; Profile/UserProfile read public user badge fields,
  ListingCard uses denormalized listing skill badges for cheap list rendering,
  and ListingDetail observes the seller user document so manual admin badge sync
  is reflected without exposing verification document metadata.
  Build verified with `./gradlew.bat :app:compileDebugKotlin`.
  → Affects `PortfolioRepository.kt`, `StorageRepository.kt`,
  `PortfolioManagerScreen.kt`, `PortfolioManagerViewModel.kt`,
  `VerificationBadges.kt`, `PortfolioComponents.kt`, `ProfileScreen.kt`,
  `ProfileViewModel.kt`, `UserProfileScreen.kt`, `UserProfileViewModel.kt`,
  `ListingCard.kt`, `ListingDetailScreen.kt`, `ListingDetailViewModel.kt`,
  `CreateListingViewModel.kt`, `EditListingViewModel.kt`, `Screen.kt`,
  `NavGraph.kt`, `AppStrings.kt`, `AGENTS.md`, `features.md`, and
  `architecture.md`.

- [2026-06-28] Phase 9 PRD P2 verification flows shipped. `VerificationRepository`
  now streams `verificationSubmissions` by user/type, creates pending identity and
  skill submissions, prevents client duplicate pending/approved submissions, and
  exposes a read-only own-portfolio stream for skill evidence selection until full
  Portfolio CRUD lands in Phase 10. Verification Center status is sourced from the
  latest submission document, while public approved badges still come from
  `users/{uid}` fields because admin sync remains manual. Identity and skill file
  uploads use private Storage paths returned by `StorageRepository` as
  `storagePath` metadata only; no KTM or skill-evidence download URL is written to
  public profile/listing fields. Build verified with `./gradlew.bat assembleDebug`.
  → Affects `VerificationRepository.kt`, `StorageRepository.kt`,
  `VerificationCenterScreen.kt`, `VerificationCenterViewModel.kt`,
  `IdentityVerificationScreen.kt`, `IdentityVerificationViewModel.kt`,
  `SkillVerificationScreen.kt`, `SkillVerificationViewModel.kt`, `NavGraph.kt`,
  `AppStrings.kt`, and `AGENTS.md`.

- [2026-06-28] Physical-device QA on Samsung SM-A235F / Android 14 verified the
  Profile → Pusat Verifikasi → Identity/Skill navigation and client validation
  paths. No crash was recorded, but Firestore runtime rules for the deployed
  project still reject the new queries: `verificationSubmissions where userId ==
  uid and type == identity|skill orderBy createdAt`, plus `portfolioItems where
  userId == uid orderBy createdAt`, all with `PERMISSION_DENIED`. Source rules
  already contain the intended matches, so deploy/verify Firebase rules before
  testing real submission writes. Logcat also shows existing profile documents may
  still contain legacy `identityVerified`; the current model field is
  `isIdentityVerified`, and the warning is non-fatal.
  → Affects `AGENTS.md`, Firebase deployment state, seeded/user profile cleanup.

- [2026-06-27] Phase 8 P1 auth/profile entry points shipped. Login now has
  Credential Manager Google sign-in wired through Firebase Auth, forgot password
  via `sendPasswordResetEmail`, and Profile links to a lightweight Verification
  Center shell that reads public user verification fields. The local
  `google-services.json` does not currently generate `default_web_client_id`
  because its `oauth_client` list is empty, so the app uses
  `app/src/main/res/values/strings.xml` key `berima_web_client_id` with fallback
  `MISSING_WEB_CLIENT_ID`; Firebase Google provider + Web Client ID must be
  configured before runtime Google login can complete.
  → Affects `AuthRepository.kt`, `LoginViewModel.kt`, `LoginScreen.kt`,
  `VerificationCenterScreen.kt`, `VerificationCenterViewModel.kt`, `ProfileScreen.kt`,
  `Screen.kt`, `NavGraph.kt`, Gradle dependency files, and `strings.xml`.

- [2026-06-27] Phase 7 P0 foundation started. `VerificationSubmission` and
  `PortfolioItem` models now exist, `User` has public verification badge fields,
  `Listing` has optional denormalized seller badge helpers and `policyAcceptedAt`,
  Firestore rules now protect badge/admin-managed fields, and `storage.rules`
  defines private owner-only verification paths. Storage and new indexes are
  configured in source files, and Firebase CLI dry-run against project
  `berima-74938` compiled the Firestore and Storage rules successfully. Deploy
  remains a separate Firebase CLI step.
  → Affects `Constants.kt`, `User.kt`, `Listing.kt`, `VerificationSubmission.kt`,
  `PortfolioItem.kt`, `firestore.rules`, `storage.rules`, `firebase.json`, and
  `firestore.indexes.json`.

- [2026-06-27] `.agents/PRD.md` is now the implementation source of truth for
  the next scope. It adds optional Verification Center, KTM-only Identity
  Verification, Skill Verification for the existing three categories, public
  portfolio items, Google login, forgot password, service policy acknowledgement,
  listing deactivation, and clearer escrow simulation copy. PDD-only items such
  as order revision, KTP support, admin panel, Cloud Functions badge sync, real
  payment, and dispute/refund remain out of scope unless explicitly requested.
  → Updated AGENTS.md and aligned `.agents/context/*.md`.
- [2026-06-27] Verification privacy rule: KTM and skill-evidence uploads must
  store private Storage paths in submission documents, never public download URLs
  in `users`, `listings`, badges, or seller display fields. Admin review is manual
  through Firebase Console for MVP. → Affects database.md, StorageRepository,
  future `storage.rules`, and verification UI/repository work.

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
- [2026-05-17] Design system fully reconciled into DESIGN.md. All tokens are
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
- [2026-05-22] Phase 4 screens wired into NavGraph. All 4 placeholder
  composables replaced: ProfileScreen, EditProfileScreen, UserProfileScreen,
  CreateReviewScreen. Build verified: `assembleDebug` BUILD SUCCESSFUL in 35s.
- [2026-05-22] `return` is prohibited inside `init` blocks in Kotlin. Use
  a null-check `if (uid != null) { ... }` guard instead of `?: return` or
  `?: run { return@init }`. The `return@init` label does not exist.
- [2026-05-22] `@OptIn(ExperimentalMaterial3Api::class)` must be on the
  composable function itself (not just the file) when `TopAppBar` is used
  inside that function. Missing opt-in causes a compile error even if the
  annotation is present elsewhere in the file.
- [2026-05-22] `BerimaTextField` extended with `singleLine` and `maxLines`
  parameters (defaults: `singleLine=true`, `maxLines=1`) so multiline fields
  (bio, comment) can reuse the shared component. All existing callers
  unaffected by the defaults.
- [2026-05-22] `@DocumentId` fields (`listingId`, `orderId`, `uid`) must NOT be written
  as Firestore document fields. Firestore populates them from the document ID at
  deserialization time. Writing them as fields causes a RuntimeException:
  "'listingId' was found from document ... cannot apply @DocumentId on this property".
  The seed script (`scripts/seed.js`) must omit these fields when calling `upsertDoc`.
- [2026-05-22] `BerimaTextField` in `ui/common/Components.kt` gained
  `singleLine: Boolean = true` and `maxLines: Int` parameters so multiline
  fields (e.g. review comment) can reuse the shared component without
  ad-hoc `OutlinedTextField` wrappers. Default is `singleLine = true` so
  all existing callers are unaffected.
- [2026-05-22] Maestro E2E suite scaffolded under `.maestro/`. Phase 4 flows
  (auth, home, search, profile, edit-profile, listing create/edit, orders
  empty states, navigation) live in `.maestro/flows/` and are runnable today.
  Phase 5 flows (listing detail, create order, user profile, create review)
  scaffolded under `.maestro/flows-phase5/` with `# TODO` markers — promote
  once Phase 5 seed data lands. Credentials read from `.maestro/.env`
  (gitignored). Runner: `.maestro/run.ps1` loads env, generates a per-run
  `BERIMA_RUN_ID`, captures HTML report + screenshots to
  `.maestro/output/<timestamp>/`. Maestro syntax gotchas hit during build:
  (a) `eraseText: 10` not `eraseText: { charactersToErase: 10 }`,
  (b) Compose `IconButton` `contentDescription` is not a Maestro `id` —
  use `back` command or `tapOn: text:` against the contentDescription string,
  (c) screen entry tap focuses the field, so subsequent re-taps on the same
  placeholder fail once text has been entered (just call `inputText` directly
  after the initial tap).
- [2026-05-22] Phase 5 Maestro flows promoted from `flows-phase5/` stubs to
  active `flows/` once Phase 5 seed data and screens were confirmed complete.
  Real selectors confirmed from source: `"Lihat profil"` (contentDescription on
  seller card arrow), `"Rating 5"` (contentDescription on 5th star in
  `StarRatingSelector`), `"Batalkan"` (buyer cancel at pending),
  `"Tulis Ulasan"` (buyer CTA at paid), `"Profil Penjual"` (UserProfileScreen
  title), `"Listing Aktif"` (UserProfileScreen section header),
  `"Catatan untuk penjual (opsional)"` / `"Tulis catatan..."` (CreateOrder note
  field label/placeholder), `"Detail Pesanan"` (OrderDetailScreen title).
  Test accounts: buyer `test+buyer@berima.dev`, seller `test+seller@berima.dev`.
  Credentials stored in `.maestro/.env` (gitignored) — never commit passwords.
  `BERIMA_TEST_PAID_ORDER_TITLE` must be set in `.env` to a seeded paid order
  title before running `63-create-review`.
- [2026-05-31] HomeScreen redesign (impeccable product register). Root-cause bug
  found and fixed: `HomeViewModel` shared one `isLoading` flag across the featured
  and main-list flows. The featured query (totalOrders) resolves first and flips
  the flag off while the main list (createdAt) is still loading, so "Terbaru"
  rendered empty even though all 10 seeded listings exist. Fix: split into
  `_featuredLoading` + `_listLoading`; Home now exposes `isListLoading` and shows
  skeleton cards for the list region instead of an all-or-nothing center spinner.
- [2026-05-31] Category-tinted thumbnail placeholders. New `CategoryColors`
  token type on `BerimaColors` (categoryAcademic/categoryVisual/categoryData),
  each a (container, glyph) pair, all within the green-only palette constraint
  (differentiated by lightness/chroma + glyph, never hue). New tokens in
  `Color.kt` + `Theme.kt`. Shared presentation helpers live in
  `ui/common/CategoryVisuals.kt`: `categoryColors()`, `categoryIconRes()`,
  `categoryLabel()`, and `categoryVisuals` (the ordered rail list). New vector
  glyphs: `ic_category_academic/visual/data.xml`. `ListingCard` now draws a
  tinted placeholder + glyph + category tag chip when `thumbnailUrl == null`,
  so listings without images stop reading as loading skeletons. This is shared,
  so Search/Profile/UserProfile inherit it.
- [2026-05-31] Home category labels shortened for a scrollable rail: Semua /
  Akademik / Desain / Data (was the full ALL-CAPS category names that overflowed
  and wrapped). Rail is a `LazyRow` of glyph+label pills (forest fill when
  active, white + hairline when not). "Terbaru" is now a 2-column
  `LazyVerticalGrid` (`GridCells.Fixed(2)`, edge items get the 16dp screen
  margin via `index % 2` padding, full-width sections use
  `GridItemSpan(maxLineSpan)`). Header band sits on `surfaceRaised` with a
  personalized greeting; `HomeViewModel` now injects `AuthRepository` and
  exposes `userFirstName` (first token of the display name). HomeScreen's inner
  Scaffold uses `contentWindowInsets = WindowInsets(0)` because the outer
  `BerimaApp` Scaffold already applies the status-bar inset (double-padding
  otherwise). Build verified: `assembleDebug` BUILD SUCCESSFUL.
- [2026-05-31] Profile + listing-form redesign (impeccable product register).
  Two NEW SHARED COMPONENTS future agents should reuse instead of rebuilding:
  1. `ui/common/CategoryPicker.kt` → `CategoryPickerField(selected, onSelected)`:
     a tappable field styled like `BerimaTextField` showing the selected
     category's glyph + full name, opening a canonical `ModalBottomSheet`
     (surface-raised, 16dp top corners, handle bar, scrim) per DESIGN.md. This
     REPLACED the old `ExposedDropdownMenu`-based `CategoryDropdown` (deleted from
     CreateListingScreen). Use this for any "pick a listing category" need.
  2. `ui/listing/ListingFormContent.kt` → `ListingFormContent(...)`: the entire
     listing form (title, category, description, price, delivery, tags), stateless
     with hoisted value/lambda params + a `submitLabel`. Both CreateListingScreen
     and EditListingScreen now render this so the field set, grouping, counters,
     and validation hints stay identical. Edit a field once, both screens update.
  Supporting additions: `ui/common/Format.kt` gained `formatRupiahInput(raw)`
  (digit-string → `Rp10.000` preview, blank for empty/zero); `CategoryVisuals.kt`
  gained `CategoryVisual.fullLabel`, `selectableCategoryVisuals` (rail list minus
  the "Semua" sentinel), and `categoryFullLabel(id)`. New vectors: `ic_camera`,
  `ic_edit`, `ic_check`, `ic_chevron_right`, `ic_chevron_down`. All new
  user-facing copy added to `AppStrings.kt` (PROFILE_*, EDIT_PROFILE_*, LISTING_*,
  CATEGORY_*) per convention #8 (no hardcoded strings in composables).
- [2026-05-31] Listing form UX fixes shipped with the redesign: Deskripsi is now
  multiline (`singleLine=false, maxLines=6`) — it was a single-line field despite
  a 500-char limit; live `n/max` counters on title (60) + description (500) that
  turn `error`-colored at the cap; price field gets an "Rp" leadingIcon + a
  formatted "Pembeli membayar RpX" preview in supportingText (money is the most
  prominent value per DESIGN.md); delivery field gets a "jam" trailingIcon. No
  ViewModel-contract or schema changes — all display logic. The shared
  `BerimaTextField` already supported leadingIcon/trailingIcon/supportingText/
  maxLines, so no signature change was needed there.
- [2026-05-31] ProfileScreen redesign details + a real bug fix: the logout action
  was a `Text("Keluar")` jammed inside a fixed-width `IconButton`, which clipped to
  "Kelua" + a stray chevron on-device. Replaced with a quiet bordered pill
  (`Box.clickable`, per the IconButton-sizing note). Layout now: IdentityCard
  (white surface, 64dp avatar, name, email, green `containerGreen` role chip, bio,
  faculty, circular edit affordance) → StatsStrip (one card, columns split by 1dp
  hairline dividers — NOT nested cards — big number + label per stat, shown by
  role) → full-width "Tambah Listing Baru" pill (was wrapping to 2 lines in a
  half-width button) → "Listing Saya" with a count → compact `ProfileListingRow`s
  (64dp category-tinted thumbnail + title + price + rating + chevron) instead of
  full-width `ListingCard`s, which were built for the grid/rail and read too heavy
  stacked. EditProfileScreen: avatar gained a forest camera badge + "Ubah Foto"
  affordance; bio switched from a hand-rolled `OutlinedTextField` to the shared
  `BerimaTextField(singleLine=false)`; role pills became a clean 3-up segmented row
  (sentence-case Pembeli/Penjual/Keduanya, forest fill when active). All three
   screens added `contentWindowInsets = WindowInsets(0)` (same double-inset fix as
   Home — they're hosted in BerimaApp's Scaffold via NavGraph innerPadding).
   Build verified: `assembleDebug` BUILD SUCCESSFUL in 38s.
- [2026-05-31] Orders redesign (list + detail) brought onto the editorial visual
  generation that Home/Profile already use. KEY CONSTRAINT: the `Order` model
  (`data/model/Order.kt`) has NO category and NO counterparty photo — only names.
  So orders can't use the category-tinted glyph language; instead they lead with
  the counterparty's identity. NEW SHARED COMPONENT: `ui/common/InitialAvatar.kt`
  → `InitialAvatar(name, size)` draws the first initial in a `containerGreen`
  circle with `primary` text. Single brand tint (no per-name hue) keeps the
  palette restrained, and circular = person per DESIGN.md. Reuse for any
  person-without-photo need. OrdersScreen card: 12dp→16dp radius, 44dp leading
  InitialAvatar, title bumped labelLarge→headlineSmall, price headlineSmall→
  headlineMedium, counterparty line reads "Penjual · Name" / "Pembeli · Name".
  Empty state upgraded to the canonical glyph(ic_orders)+headline+body layout.
  OrderDetailScreen restructured with a "card for objects, open for content"
  rhythm to avoid card-soup: summary card (price bumped to displayMedium, +chevron)
  → OPEN status block on cream (overline label + chip + NEW contextual sentence
  varying by status×role + haloed-dot timeline) → note card (overline label, no
  side-stripe) → attachment row (+chevron) → counterparty person row (InitialAvatar
  + role overline + name, display-only) → actions → chat. Chat empty state fixed:
  was a 200dp void with one centered line; now a compact ~120dp hint (mail glyph
  in green circle + "Belum ada pesan" + "Mulai percakapan dengan X"), heightIn min
  lowered 200dp→120dp. OrderStatusTimeline current dot now gets a containerGreen
  halo ring ("you are here"). All new copy in AppStrings (ORDERS_*, ORDER_DETAIL_*,
  ORDER_STATUS_*, ORDER_CHAT_*) per convention #8. NO model/ViewModel/repo logic
  changes — pure UI. Gotcha: OrdersScreen needed `layout.size` import added for the
  empty-state icon. Build verified: `assembleDebug` BUILD SUCCESSFUL in 27s.
