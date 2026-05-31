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
| CreateListingScreen + CreateListingViewModel | ✅ Done | Form with validation, category dropdown, tags |
| EditListingScreen | ✅ Done | Pre-filled form, reuses CategoryDropdown from CreateListingScreen |
| ListingCard composable (reusable) | ✅ Done | `ui/common/ListingCard.kt` |

### Phase 3 — Order Flow
| Task | Status | Notes |
|---|---|---|
| CreateOrderScreen + CreateOrderViewModel | ✅ Done | Loads listing via one-shot `getListing`, denormalizes buyer/seller fields onto Order, optional 300-char note |
| OrdersScreen + OrdersViewModel | ✅ Done | `PrimaryTabRow` with Pembeli / Penjual tabs, BI relative timestamps, empty states per tab |
| OrderDetailScreen + OrderDetailViewModel | ✅ Done | Two-VM split (detail + chat), centralized `OrderAction` dispatch, role × status action matrix, file picker for result upload |
| Chat section inside OrderDetailScreen | ✅ Done | Bounded LazyColumn (200–480dp), auto-scroll on new message, ➤ glyph send button (no `material-icons-extended` dep) |
| StatusChip composable (reusable) | ✅ Done | `ui/common/StatusChip.kt`, all 7 statuses mapped to design tokens, pill shape |

### Phase 4 — Profile & Review
| Task | Status | Notes |
|---|---|---|
| ProfileScreen + ProfileViewModel | ✅ Done | Own profile: avatar, bio, faculty, role badge, stats, listings column, edit + create buttons |
| EditProfileScreen + EditProfileViewModel | ✅ Done | Photo picker, multiline bio, role chips, `uploadProfilePhoto` added to StorageRepository |
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

**Status legend:** ⬜ Not started · 🔄 In progress · ✅ Done · ⚠️ Blocked

---

## Learned
