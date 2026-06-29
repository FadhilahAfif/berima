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
6. `DESIGN.md` — design system, colors, typography, UI components
7. `.agents/context/learned.md` — project discoveries and modifications
8. `.agents/PDD.md` — product direction and long-term product intent
9. `.agents/PRD.md` — implementable requirements for the next development scope

---

## Source of Truth

- `AGENTS.md` defines how agents must work in this repository.
- `.agents/PRD.md` is the implementation source of truth for the next feature scope.
- `.agents/PDD.md` is product direction only. Do not implement PDD items that PRD marks out of scope.
- `.agents/context/features.md` and `.agents/context/database.md` describe the already-built MVP baseline. When implementing PRD work, use them to understand existing behavior, then update them as the implementation changes.
- `architecture.md`, `conventions.md`, and `DESIGN.md` remain hard technical and UI constraints.
- If documents conflict: follow `AGENTS.md` first, then `.agents/PRD.md`, then the context files, then `.agents/PDD.md`.

---

## Hard Rules

- Read ALL required files before writing any code
- Never deviate from the architecture in `architecture.md`
- Never add features outside `.agents/PRD.md` or `.agents/context/features.md` unless explicitly told to
- Never implement PDD-only items that PRD explicitly defers or rejects
- Always follow patterns in `conventions.md` — no exceptions
- All user-facing text must be in **Bahasa Indonesia**
- All code, comments, and variable names must be in **English**
- Verification identity documents are private. Never store KTM download URLs in public profile, listing, or badge fields.
- Admin review for verification is manual through Firebase Console only for MVP. Do not build an in-app admin panel unless explicitly requested.
- Terminology policy: user-facing copy should prefer `layanan`, `pemesan`, and `penyedia jasa`. Keep `buyer`, `seller`, and `listing` only for internal code, schema fields, or docs that explicitly describe technical identifiers. When in doubt, choose the service-first term in visible UI text.

---

## How to Handle a Task

1. Check the **Milestone** section below — understand what is done and what is not
2. Identify which screen or feature is being requested
3. Cross-reference `.agents/PRD.md` for requirement IDs and acceptance criteria
4. Cross-reference `features.md` for existing behavior and `database.md` for current data shape
5. Cross-reference `conventions.md` for implementation pattern
6. Cross-reference `DESIGN.md` for UI decisions and reusable component expectations
7. Build it — do not ask unnecessary clarifying questions if the answer is in the required files
8. After completing a task, **update the Milestone section** to reflect current progress
9. If the task changes implemented behavior, update the relevant context doc and add a note to `.agents/context/learned.md`

---

## Self-Improvement Rule

If during development you discover something that should be documented — a pattern that works
well, a gotcha with a library, a decision made on the fly, a deviation from the original spec —
**update the relevant context file immediately** and note it in `.agents/context/learned.md`.

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
| SplashScreen | ✅ Done | Logo + halo + delayed pulsing-dots loader, staggered entry, auth-state routing |
| LoginScreen + LoginViewModel | ✅ Done | Editorial 2-line headline, brand-mark chip, leading-icon fields, staggered entry |
| RegisterScreen + RegisterViewModel | ✅ Done | Editorial 2-line headline, brand-mark chip, leading-icon fields, password supportingText |

### Phase 2 — Core Listing
| Task | Status | Notes |
|---|---|---|
| HomeScreen + HomeViewModel | ✅ Done | Redesigned: surfaceRaised header band + personalized greeting, scrollable glyph category rail, featured rail, 2-col grid, skeleton loading, real empty state. Fixed shared-loading bug that blanked "Terbaru". Category-tinted ListingCard placeholders (shared). |
| SearchScreen | ✅ Done | Client-side title filter, auto-focus, empty/no-results states |
| ListingDetailScreen + ListingDetailViewModel | ✅ Done | Full detail view, penyedia jasa card, reviews section, owner/pemesan CTA |
| CreateListingScreen + CreateListingViewModel | ✅ Done | Redesigned: renders shared `ListingFormContent` (3 labelled sections, char counters, multiline desc, Rp-prefixed price + live preview, bottom-sheet category picker). Now supports one gallery thumbnail upload to Storage. |
| EditListingScreen | ✅ Done | Pre-filled form, renders the same shared `ListingFormContent` as Create. Existing thumbnail can be replaced or cleared. |
| ListingCard composable (reusable) | ✅ Done | `ui/common/ListingCard.kt`; uses uploaded thumbnails when present and generated category fallback images when absent |
| CategoryPicker composable (reusable) | ✅ Done | `ui/common/CategoryPicker.kt` — `CategoryPickerField` + bottom sheet, replaced old `CategoryDropdown`. Reuse for any category selection. |
| ListingFormContent composable (reusable) | ✅ Done | `ui/listing/ListingFormContent.kt` — stateless full listing form shared by Create + Edit, including thumbnail picker/preview. |

### Phase 3 — Order Flow
| Task | Status | Notes |
|---|---|---|
| CreateOrderScreen + CreateOrderViewModel | ✅ Done | Loads listing via one-shot `getListing`, denormalizes buyer/seller fields onto Order, optional 300-char note |
| OrdersScreen + OrdersViewModel | ✅ Done | Redesigned (editorial gen): 16dp cards w/ 44dp leading `InitialAvatar`, headlineSmall title + headlineMedium price, "Penyedia Jasa · Name" line, canonical glyph empty state. `PrimaryTabRow` Pemesan/Penyedia. |
| OrderDetailScreen + OrderDetailViewModel | ✅ Done | Redesigned: card-for-objects/open-for-content rhythm — summary (displayMedium price) → open status block (chip + contextual sentence + haloed-dot timeline) → note → attachment → counterparty person row → actions → chat. |
| Chat section inside OrderDetailScreen | ✅ Done | Bounded LazyColumn (200–480dp), auto-scroll on new message, ➤ glyph send button (no `material-icons-extended` dep) |
| StatusChip composable (reusable) | ✅ Done | `ui/common/StatusChip.kt`, all 7 statuses mapped to design tokens, pill shape |

### Phase 4 — Profile & Review
| Task | Status | Notes |
|---|---|---|
| ProfileScreen + ProfileViewModel | ✅ Done | Redesigned: IdentityCard (avatar, role chip, bio, faculty, edit affordance), StatsStrip (hairline-divided columns), full-width CTA, compact `ProfileListingRow`s. Fixed logout-button clipping bug + insets. |
| EditProfileScreen + EditProfileViewModel | ✅ Done | Redesigned: avatar camera badge + "Ubah Foto", multiline bio via shared `BerimaTextField`, 3-up segmented role control, counters. `uploadProfilePhoto` on StorageRepository. |
| UserProfileScreen + UserProfileViewModel | ✅ Done | Read-only penyedia jasa profile, active layanan, stats row |
| CreateReviewScreen + CreateReviewViewModel | ✅ Done | Star selector, multiline comment, rating validation, isSubmitted nav |

### Phase 5 — Polish & Demo Readiness
| Task | Status | Notes |
|---|---|---|
| Demo data seeded (10 listings, 3 sellers, 3 orders) | ✅ Done | `scripts/seed.js` via Firestore REST API; 4 users, 10 listings, 3 orders seeded |
| Firestore security rules deployed | ✅ Done | `firestore.rules` + `firebase.json` created, deployed via `firebase deploy --only firestore:rules` |
| Firestore composite indexes created | ✅ Done | `firestore.indexes.json` deployed via Firebase CLI |
| Maestro E2E test suite scaffolded | ✅ Done | `.maestro/` — 15 flows across `flows/` (Phase 4 + Phase 5 active), stubs archived in `flows-phase5/` |
| App tested on 2 physical Android devices | ⬜ Not started | |
| No crash on full demo flow (browse → order → review) | ⬜ Not started | |

### Phase 6 — PRD Alignment
| Task | Status | Notes |
|---|---|---|
| Product Definition Document reviewed | ✅ Done | `.agents/PDD.md` audited against the existing app |
| Product Requirements Document created | ✅ Done | `.agents/PRD.md` is the next-scope implementation source of truth |
| AGENTS.md aligned to PRD scope | ✅ Done | Required reading, source-of-truth rules, and future milestones updated |
| Context docs aligned to PRD scope | ✅ Done | `.agents/context/*.md` updated so future implementation does not follow stale PDD/context conflicts |

### Phase 7 — PRD P0: Security & Schema Foundation
| Task | Status | Notes |
|---|---|---|
| Verification and portfolio models/constants | ✅ Done | Added `VerificationSubmission`, `PortfolioItem`, verification constants, user badge fields, listing badge/policy fields |
| Firestore rules for verification, portfolio, and protected badge fields | ✅ Done | Deployed to `berima-74938` on 2026-06-28 and verified on physical device; previous Profile/Verification `PERMISSION_DENIED` is resolved. |
| Storage rules configured and deployed | ✅ Done | `storage.rules` + `firebase.json` storage config deployed to `berima-74938` on 2026-06-28; listing thumbnails and demo assets upload/read successfully. |
| Firestore composite indexes for verification and portfolio | ✅ Done | Added indexes from PRD Section 5 to `firestore.indexes.json`; Firebase CLI dry-run passed |

### Phase 8 — PRD P1: Auth & Profile Entry Points
| Task | Status | Notes |
|---|---|---|
| Google login/register | ✅ Done | Credential Manager + Google ID token wired to Firebase Auth; creates minimal profile when absent; requires Firebase Google provider + `berima_web_client_id` Web Client ID |
| Forgot password | ✅ Done | Email/password reset flow from LoginScreen with Bahasa Indonesia snackbar feedback |
| Verification Center route and Profile entry | ✅ Done | Route, Profile status summary entry, and lightweight VerificationCenter shell added |

### Phase 9 — PRD P2: Verification Flows
| Task | Status | Notes |
|---|---|---|
| VerificationCenterScreen + ViewModel | ✅ Done | Reads latest submission docs for identity/skill status, shows rejection reason, active badges, and CTAs |
| IdentityVerificationScreen + ViewModel | ✅ Done | KTM-only private upload, optional note, pending/rejected/approved states, rejected resubmission |
| SkillVerificationScreen + ViewModel | ✅ Done | Existing categories only; supports portfolio item, optional link, optional private evidence file |
| VerificationRepository | ✅ Done | Submission read/create flows plus read-only own portfolio stream; no Firebase types exposed to ViewModels |

### Phase 10 — PRD P3: Portfolio & Badges
| Task | Status | Notes |
|---|---|---|
| Portfolio CRUD | ✅ Done | `PortfolioRepository` + `PortfolioManagerScreen`; title, description, category, optional link, one optional image; edit/delete supported |
| Public portfolio display | ✅ Done | Profile shows own portfolio preview + manage entry; UserProfile shows public portfolio with images and safe external links |
| Identity and skill badge components | ✅ Done | `VerificationBadgeRow` + listing skill badge, Bahasa Indonesia labels, no document data exposed |
| Badge placement across marketplace | ✅ Done | Profile, UserProfile, ListingCard, and ListingDetail penyedia jasa info wired; ListingDetail reads seller user badge fields as display source |

### Phase 11 — PRD P4: Listing & Order Polish
| Task | Status | Notes |
|---|---|---|
| Service policy acknowledgement | ✅ Done | `ListingFormContent` now requires a policy checkbox before Create/Edit save and writes `policyAcceptedAt` |
| Listing deactivation UI | ✅ Done | Profile, ListingDetail, and EditListing expose owner-only deactivation through existing `setListingActive`; inactive listings are labelled in own Profile |
| Escrow simulation copy update | ✅ Done | Order detail/action copy keeps `Simulasi Bayar` but clarifies no real money is processed and Midtrans/payment gateway is future placeholder |
| PR #31 review cleanup | ✅ Done | Listing thumbnails persist `thumbnailStoragePath` for cleanup, user-owned Storage paths allow delete, the policy row is a single toggle target, and payment copy stays fully Bahasa Indonesia |
| Terminology refresh | ✅ Done | User-facing copy now prefers layanan/penyedia jasa/pemesan over listing/penjual/pembeli where it reads as marketplace language |

### Phase 12 — Demo Readiness Plan
| Task | Status | Notes |
|---|---|---|
| Codebase capability audit | ✅ Done | 2026-06-28 audit confirms source contains the complete marketplace demo surface: email auth, Google auth wiring, forgot password, browse/search layanan, create/edit/deactivate layanan, thumbnail upload, order lifecycle, chat, result upload, simulated payment, review, profile, verification center, identity/skill submissions, portfolio CRUD, and badge display. |
| Local compile verification | ✅ Done | `./gradlew.bat :app:compileDebugKotlin` succeeded on 2026-06-28. Only warning observed is the Kotlin annotation default-target warning in `StorageRepository`; no compile blocker. |
| Firebase rules and indexes dry-run | ✅ Done | `firebase deploy --only firestore:rules,firestore:indexes,storage --dry-run --project berima-74938` succeeded on 2026-06-28; Firestore and Storage rules compile locally. |
| Deploy latest Firestore rules, Storage rules, and indexes to live Firebase project | ✅ Done | Deployed on 2026-06-28. Runtime smoke found index-build blockers, then repository queries were adjusted to sort verification/portfolio data client-side so demo screens do not depend on newly building composite indexes. |
| Google login production readiness check | 🔄 In progress | `berima_web_client_id` is present in `strings.xml`; still verify Firebase Google provider, SHA config, and real device sign-in before demo. Keep email/password demo accounts as fallback. |
| Demo dataset refresh for business story | ✅ Done | `scripts/seed-demo.js` generated/uploaded six custom service thumbnails and seeded a reusable Firebase demo dataset: 1 pemesan, 2 penyedia jasa, active layanan across all 3 categories, verified identity/skill badges, 2 portfolio items, pending + in-progress + paid-review-ready + reviewed orders. |
| End-to-end buyer demo rehearsal | ⬜ Not started | Run browse → search/filter → listing detail → create order → chat → seller accepts/delivers → buyer confirms → Simulasi Bayar → review. Record blockers and fix before presentation. |
| End-to-end seller/trust demo rehearsal | ⬜ Not started | Run profile edit → create layanan with policy acknowledgement → portfolio CRUD → identity/skill verification submission → manual Firebase Console approval story → badge appears on Profile/UserProfile/ListingCard/ListingDetail. |
| Maestro regression suite run on current APK | ⬜ Not started | Run `.maestro/run.ps1` after installing the latest debug APK and refreshing `.maestro/.env`. Treat failures as demo blockers only when they affect the planned presentation path. |
| Physical-device smoke test on 2 Android devices | 🔄 In progress | 1 device verified: Samsung SM-A235F / Android 14. Build/install, Home demo data, custom thumbnails, ListingDetail, Profile, Portfolio preview, and Verification Center open without crash or Firestore permission/index errors. Second device still pending. |
| Demo script and fallback checklist | ⬜ Not started | Prepare a timed presenter script in Bahasa Indonesia plus backup paths: email/password login if Google fails, seeded screenshots/data if network is unstable, and Firebase Console steps for manual verification approval. |
| UI/UX redesign audit plan | ✅ Done | `docs/UI_UX_REDESIGN_PLAN.md` captures the no-implementation audit and staged redesign backlog after device review of Login, Home, ListingDetail, Orders, Profile, and Verification Center. |
| UI/UX redesign foundation pass | ✅ Done | Added semantic action buttons, danger/inline action hierarchy, Profile CTA/trust summary fixes, compact badge copy, ListingDetail affordance/type fixes, Home rail density tuning, review touch-target fixes, and top bar scale alignment. Build and install verified on Samsung SM-A235F / Android 14. |
| Equal layanan card sizing | ✅ Done | Shared `ListingCard` now uses fixed 304dp height with bottom-anchored seller row; Home skeleton cards match the same height and Profile layanan rows are fixed at 132dp. Build/install and device screenshot verified on Samsung SM-A235F / Android 14. |
| ListingDetail mobile action polish | ✅ Done | Listing Detail now has a sticky bottom action bar and a stronger price/category/delivery panel; compile, install, and device UI-tree/screenshot checks verified on Samsung SM-A235F / Android 14. |
| Profile/EditProfile mobile polish | ✅ Done | Profile identity now folds stats into the main header; Edit Profile uses sectioned form panels and a sticky save bar. Compile, install, and device UI-tree checks verified on Samsung SM-A235F / Android 14. |

**Status legend:** ⬜ Not started · 🔄 In progress · ✅ Done · ⚠️ Blocked

---

## Learned

- [2026-06-29] Profile order stats now show one completed-order count for the
  provider role instead of separate buyer/seller counters. The displayed value is
  `totalOrdersAsSeller`, labelled `PESANAN SELESAI`, so the profile no longer
  shows confusing role counters like `42 PENYEDIA`.
  → Affects `ProfileScreen.kt`, `AppStrings.kt`, `.agents/context/learned.md`,
  and `AGENTS.md`.

- [2026-06-29] Profile and Edit Profile now read more like native mobile account
  surfaces instead of stacked generic cards. Profile folds layanan count, rating,
  and order stats into the identity header; Edit Profile groups public info and
  role choice into form sections with a sticky `Simpan Perubahan` bar. Verified
  with `./gradlew.bat :app:compileDebugKotlin`, `./gradlew.bat :app:installDebug`,
  and device UI-tree checks on Samsung SM-A235F / Android 14.
  → Affects `ProfileScreen.kt`, `EditProfileScreen.kt`, `AppStrings.kt`,
  `.agents/context/learned.md`, and `AGENTS.md`.

- [2026-06-29] Listing Detail now behaves more like a native service-commerce
  screen: the primary order/edit action is in a sticky bottom bar, while the
  scroll content uses a dedicated price/category/delivery panel instead of loose
  metadata text. Verified with `./gradlew.bat :app:compileDebugKotlin`,
  `./gradlew.bat :app:installDebug`, and device UI-tree/screenshot checks on
  Samsung SM-A235F / Android 14.
  → Affects `ListingDetailScreen.kt`, `.agents/context/learned.md`, and
  `AGENTS.md`.

- [2026-06-28] Service card sizing was standardized after device QA showed card
  height could vary by title, badge, and rating content. Shared `ListingCard`
  now defaults to a fixed 304dp height, anchors the seller row to the bottom, and
  Home skeleton cards use the same 304dp height to avoid layout jumps.
  Profile-owned service rows are fixed at 132dp. Keep future layanan card
  variants aligned to these dimensions unless the design system is deliberately
  revised.
  → Affects `ListingCard.kt`, `HomeScreen.kt`, `ProfileScreen.kt`, `DESIGN.md`,
  and `.agents/context/learned.md`.

- [2026-06-28] UI/UX redesign foundation pass shipped from
  `docs/UI_UX_REDESIGN_PLAN.md`. Shared semantic actions now live in
  `ui/common/Components.kt` (`SecondaryActionButton`, `DangerActionButton`,
  `InlineTextAction`) so destructive and inline actions no longer reuse primary
  green styling. Edit Listing and Listing Detail now render `Nonaktifkan layanan`
  as outline danger, OrderActions shares the same danger vocabulary, Profile moves
  `Tambah Layanan Baru` above Portfolio and splits verification status into
  readable identity/skill rows, Home category rail density was adjusted so all
  four chips fit on the Samsung SM-A235F viewport, ListingCard compact badges now
  say `Ahli <kategori>`, ListingDetail uses a chevron-right affordance for
  `Lihat profil`, and CreateReview star targets are 44dp. Verified with
  `./gradlew.bat :app:compileDebugKotlin`, `./gradlew.bat :app:installDebug`,
  and manual device QA on Samsung SM-A235F / Android 14.
  → Affects `Components.kt`, `AppStrings.kt`, `HomeScreen.kt`,
  `EditListingScreen.kt`, `ListingDetailScreen.kt`, `ListingFormContent.kt`,
  `OrderActions.kt`, `CreateReviewScreen.kt`, Profile/verification/order top bars,
  `docs/UI_UX_REDESIGN_PLAN.md`, and `.agents/context/learned.md`.

- [2026-06-28] UI/UX redesign audit completed without changing app UI. The plan
  lives in `docs/UI_UX_REDESIGN_PLAN.md` and prioritizes semantic action
  hierarchy, Profile bottom-navigation clipping, type scale calibration, minimum
  touch targets, top app bar consistency, Home rail density, compact trust badges,
  and ListingDetail seller-card affordance fixes. `PRODUCT.md` was added because
  the impeccable audit workflow requires a product-register context file before
  design audit work.

- [2026-06-28] Demo readiness audit completed. The codebase compiles and the
  current source covers the full marketplace + PRD trust surface, including
  verification, portfolio, badges, service policy, listing deactivation, and
  simulated payment copy. Firebase rules/index/storage dry-run succeeds, but
  the live Firebase project still needs deploy/verification before claiming the
  verification and portfolio paths are demo-ready on device. The next practical
  work is not new feature implementation; it is live Firebase deploy, seeded
  trust/demo data, two-device QA, Maestro regression, and a presenter script.
- [2026-06-28] Demo dataset is now seeded through `scripts/seed-demo.js`. The
  script generates six custom PNG thumbnails under `scripts/demo-assets/`,
  uploads them to Firebase Storage, and upserts deterministic Firestore demo
  documents for ethical services only: presentation design, campus poster,
  Excel dashboard, proofreading, reference formatting, and CV design. The demo
  data includes approved public badge fields, portfolio items, and order states
  useful for a business walkthrough. It reuses the existing buyer/seller auth
  UIDs by default and can reuse the local Firebase CLI token after `firebase
  login`.
- [2026-06-28] Android device QA via the test-android-apps workflow found and
  resolved the live Firebase blockers. Firestore and Storage rules/indexes were
  deployed to `berima-74938`; verification/portfolio reads no longer fail with
  `PERMISSION_DENIED`. Firestore composite indexes for portfolio/verification
  were still building during QA, so `PortfolioRepository` and
  `VerificationRepository` now query by `userId`/`type` only and sort by
  `createdAt` in memory. This keeps Profile and Verification Center demo-safe
  even before new indexes finish building.
- [2026-06-28] Phase 11 P4 listing/order polish shipped. Create/Edit Listing now
  require a service-policy checkbox before save, write `policyAcceptedAt`, and use
  concise Bahasa Indonesia copy prohibiting joki, plagiarism, document
  falsification, and illegal work. Owners can deactivate active listings from
  Profile, ListingDetail, or EditListing via `ListingRepository.setListingActive`;
  inactive listings remain visible in the owner's Profile with a `NONAKTIF` label
  but are hidden from public active-listing queries. Order detail keeps the current
  lifecycle and `Simulasi Bayar` action while clarifying that MVP payment is a demo
  with no real money processed.
- [2026-06-28] Listing thumbnails now support user gallery upload. Create/Edit
  preview one selected image, upload it to `users/{uid}/listings/{listingId}/{filename}`,
  and store the public URL in `listings.thumbnailUrl`; edit can replace or clear the
  thumbnail. Generated fallback images live in `app/src/main/res/drawable-nodpi/`
  for academic/visual/data listings when `thumbnailUrl` is null.
- [2026-06-27] Phase 8 uses Android Credential Manager + Google ID token for
  Google auth. Runtime login requires Firebase Google provider and a Web Client
  ID in `app/src/main/res/values/strings.xml` as `berima_web_client_id`; current
  fallback is `MISSING_WEB_CLIENT_ID` because local `google-services.json` has no
  OAuth client entries.
- [2026-06-28] Phase 10 P3 portfolio and badges shipped. Portfolio CRUD lives in
  `ui/portfolio/PortfolioManagerScreen.kt` with `PortfolioRepository`; Profile
  shows a preview and management entry, UserProfile shows public portfolio items,
  and badge display is centralized in `ui/common/VerificationBadges.kt`.
