# Berima — Agent Instructions

You are building **Berima**, a mobile marketplace app for student micro-gigs.
Before doing anything, read all context files in order.

---

## Required Reading (in order)

1. `.agents/context/project.md` — what the app is, who it's for, what problem it solves
2. `.agents/context/features.md` — full feature spec, screen list, behavior per screen
3. `.agents/context/database.md` — Firestore schema, security rules, required indexes
4. `.agents/context/architecture.md` — tech stack, folder structure, dependencies
5. `.agents/context/conventions.md` — coding rules, patterns, naming — follow these always

---

## Hard Rules

- Read ALL context files before writing any code
- Never deviate from the architecture in `architecture.md`
- Never add features outside the scope in `features.md` unless explicitly told to
- Always follow patterns in `conventions.md` — no exceptions
- All user-facing text must be in **Bahasa Indonesia**
- All code, comments, and variable names must be in **English**

---

## How to Handle a Task

1. Check the **Milestone** section below — understand what is done and what is not
2. Identify which screen or feature is being requested
3. Cross-reference `features.md` for behavior spec
4. Cross-reference `database.md` for data shape
5. Cross-reference `conventions.md` for implementation pattern
6. Build it — do not ask unnecessary clarifying questions if the answer is in the context files
7. After completing a task, **update the Milestone section** to reflect current progress

---

## Self-Improvement Rule

If during development you discover something that should be documented — a pattern that works
well, a gotcha with a library, a decision made on the fly, a deviation from the original spec —
**update the relevant context file immediately** and note it in the Learned section below.

Do not wait. Document it when you find it, so the next agent session or team member has
accurate context. Stale documentation is worse than no documentation.

---

## Milestones

Track progress here. Update status after each completed task.
One agent session should update at minimum one item before ending.

### Phase 1 — Foundation
| Task | Status | Notes |
|---|---|---|
| Project setup (Hilt, Firebase, Navigation) | ✅ Done | Gradle + version catalog wired |
| All data classes (User, Listing, Order, Review, Message) | ✅ Done | Plus `Constants.kt` with OrderStatus / Category / Validation |
| All repository classes | ✅ Done | Auth, Listing, Order, Review, Message |
| AppModule.kt (Hilt DI) | ✅ Done | Provides FirebaseAuth, Firestore, Storage |
| NavGraph.kt + Screen.kt | ✅ Done | All routes wired to placeholder composables |
| SplashScreen | ✅ Done | Animated logo + tagline, auth-state routing |
| LoginScreen + LoginViewModel | ✅ Done | Email/password, domain validation, snackbar errors |
| RegisterScreen + RegisterViewModel | ✅ Done | 4-field form, auto-login on success |

### Phase 2 — Core Listing
| Task | Status | Notes |
|---|---|---|
| HomeScreen + HomeViewModel | ⬜ Not started | |
| SearchScreen | ⬜ Not started | |
| ListingDetailScreen + ListingDetailViewModel | ⬜ Not started | |
| CreateListingScreen + CreateListingViewModel | ⬜ Not started | |
| EditListingScreen | ⬜ Not started | |
| ListingCard composable (reusable) | ⬜ Not started | |

### Phase 3 — Order Flow
| Task | Status | Notes |
|---|---|---|
| CreateOrderScreen + CreateOrderViewModel | ⬜ Not started | |
| OrdersScreen + OrdersViewModel | ⬜ Not started | |
| OrderDetailScreen + OrderDetailViewModel | ⬜ Not started | |
| Chat section inside OrderDetailScreen | ⬜ Not started | |
| StatusChip composable (reusable) | ⬜ Not started | |

### Phase 4 — Profile & Review
| Task | Status | Notes |
|---|---|---|
| ProfileScreen + ProfileViewModel | ⬜ Not started | |
| EditProfileScreen | ⬜ Not started | |
| UserProfileScreen | ⬜ Not started | |
| CreateReviewScreen + CreateReviewViewModel | ⬜ Not started | |

### Phase 5 — Polish & Demo Readiness
| Task | Status | Notes |
|---|---|---|
| Demo data seeded (10 listings, 3 sellers, 3 orders) | ⬜ Not started | |
| Firestore security rules deployed | ⬜ Not started | |
| Firestore composite indexes created | ⬜ Not started | |
| App tested on 2 physical Android devices | ⬜ Not started | |
| No crash on full demo flow (browse → order → review) | ⬜ Not started | |

**Status legend:** ⬜ Not started · 🔄 In progress · ✅ Done · ⚠️ Blocked

---

## Learned

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
