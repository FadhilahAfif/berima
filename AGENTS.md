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
6. `DESIGN.MD` — design system, colors, typography, UI components
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
