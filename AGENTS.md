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
| Project setup (Hilt, Firebase, Navigation) | ⬜ Not started | |
| All data classes (User, Listing, Order, Review, Message) | ⬜ Not started | |
| All repository classes | ⬜ Not started | |
| AppModule.kt (Hilt DI) | ⬜ Not started | |
| NavGraph.kt + Screen.kt | ⬜ Not started | |
| SplashScreen | ⬜ Not started | |
| LoginScreen + LoginViewModel | ⬜ Not started | |
| RegisterScreen + RegisterViewModel | ⬜ Not started | |

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

> This section starts empty. Add entries as the project runs.

<!-- Example format:
- [2025-01-15] Firestore `toObject()` fails silently if a field has no default value in the
  data class. All data class fields MUST have defaults. → Updated conventions.md
- [2025-01-16] Navigation Compose back stack behaves unexpectedly when navigating from
  OrderDetail to CreateReview then popping — use `popUpTo` with `inclusive = false`.
  → Added note to architecture.md
-->
