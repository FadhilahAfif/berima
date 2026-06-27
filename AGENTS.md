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
| ListingDetailScreen + ListingDetailViewModel | ✅ Done | Full detail view, seller card, reviews section, owner/buyer CTA |
| CreateListingScreen + CreateListingViewModel | ✅ Done | Redesigned: renders shared `ListingFormContent` (3 labelled sections, char counters, multiline desc, Rp-prefixed price + live preview, bottom-sheet category picker). |
| EditListingScreen | ✅ Done | Pre-filled form, renders the same shared `ListingFormContent` as Create. |
| ListingCard composable (reusable) | ✅ Done | `ui/common/ListingCard.kt` |
| CategoryPicker composable (reusable) | ✅ Done | `ui/common/CategoryPicker.kt` — `CategoryPickerField` + bottom sheet, replaced old `CategoryDropdown`. Reuse for any category selection. |
| ListingFormContent composable (reusable) | ✅ Done | `ui/listing/ListingFormContent.kt` — stateless full listing form shared by Create + Edit. |

### Phase 3 — Order Flow
| Task | Status | Notes |
|---|---|---|
| CreateOrderScreen + CreateOrderViewModel | ✅ Done | Loads listing via one-shot `getListing`, denormalizes buyer/seller fields onto Order, optional 300-char note |
| OrdersScreen + OrdersViewModel | ✅ Done | Redesigned (editorial gen): 16dp cards w/ 44dp leading `InitialAvatar`, headlineSmall title + headlineMedium price, "Penjual · Name" line, canonical glyph empty state. `PrimaryTabRow` Pembeli/Penjual. |
| OrderDetailScreen + OrderDetailViewModel | ✅ Done | Redesigned: card-for-objects/open-for-content rhythm — summary (displayMedium price) → open status block (chip + contextual sentence + haloed-dot timeline) → note → attachment → counterparty person row → actions → chat. |
| Chat section inside OrderDetailScreen | ✅ Done | Bounded LazyColumn (200–480dp), auto-scroll on new message, ➤ glyph send button (no `material-icons-extended` dep) |
| StatusChip composable (reusable) | ✅ Done | `ui/common/StatusChip.kt`, all 7 statuses mapped to design tokens, pill shape |

### Phase 4 — Profile & Review
| Task | Status | Notes |
|---|---|---|
| ProfileScreen + ProfileViewModel | ✅ Done | Redesigned: IdentityCard (avatar, role chip, bio, faculty, edit affordance), StatsStrip (hairline-divided columns), full-width CTA, compact `ProfileListingRow`s. Fixed logout-button clipping bug + insets. |
| EditProfileScreen + EditProfileViewModel | ✅ Done | Redesigned: avatar camera badge + "Ubah Foto", multiline bio via shared `BerimaTextField`, 3-up segmented role control, counters. `uploadProfilePhoto` on StorageRepository. |
| UserProfileScreen + UserProfileViewModel | ✅ Done | Read-only seller profile, active listings, stats row |
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
| Firestore rules for verification, portfolio, and protected badge fields | ✅ Done | Source updated: users cannot self-approve/edit badge fields; submissions are owner-only; portfolio is auth-readable owner-writable |
| Storage rules configured and deployed | 🔄 In progress | `storage.rules` + `firebase.json` storage config added locally; Firebase CLI dry-run passed; deploy still pending |
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
| VerificationCenterScreen + ViewModel | ⬜ Not started | Identity and skill cards with status, rejection reason, CTA |
| IdentityVerificationScreen + ViewModel | ⬜ Not started | KTM-only upload, pending/rejected/approved states, resubmission |
| SkillVerificationScreen + ViewModel | ⬜ Not started | Existing categories only: academic, visual, data; portfolio/link/evidence |
| VerificationRepository | ⬜ Not started | Submission CRUD/read flows, no Firebase types exposed to ViewModels |

### Phase 10 — PRD P3: Portfolio & Badges
| Task | Status | Notes |
|---|---|---|
| Portfolio CRUD | ⬜ Not started | Title, description, category, optional link, one optional image |
| Public portfolio display | ⬜ Not started | Own Profile management + read-only UserProfile display |
| Identity and skill badge components | ⬜ Not started | Reusable badge UI, Bahasa Indonesia labels, no document data exposed |
| Badge placement across marketplace | ⬜ Not started | Profile, UserProfile, ListingCard, ListingDetail seller info |

### Phase 11 — PRD P4: Listing & Order Polish
| Task | Status | Notes |
|---|---|---|
| Service policy acknowledgement | ⬜ Not started | Required in Create/Edit Listing; prohibits joki/plagiarism/document falsification |
| Listing deactivation UI | ⬜ Not started | Use existing `setListingActive`; do not expose hard delete in MVP client |
| Escrow simulation copy update | ⬜ Not started | Keep existing lifecycle; clarify no real payment is processed |

**Status legend:** ⬜ Not started · 🔄 In progress · ✅ Done · ⚠️ Blocked

---

## Learned

- [2026-06-27] Phase 8 uses Android Credential Manager + Google ID token for
  Google auth. Runtime login requires Firebase Google provider and a Web Client
  ID in `app/src/main/res/values/strings.xml` as `berima_web_client_id`; current
  fallback is `MISSING_WEB_CLIENT_ID` because local `google-services.json` has no
  OAuth client entries.
