# Berima PRD

**Version:** 1.0  
**Status:** Approved for implementation planning  
**Last Updated:** 2026-06-27  
**Source Direction:** `.agents/PDD.md` plus audit of current Android/Firebase implementation

---

## 1. Summary

This PRD defines the next implementable Berima MVP scope. It is not a copy of the PDD. It translates the product direction into requirements that fit the current Android app architecture: Jetpack Compose, MVVM, Hilt, Firebase Auth, Firestore, and Firebase Storage.

The core product addition is an optional **Verification Center** from the Profile screen. Users may continue using Berima without verification, but verified identity and skill badges improve trust across profiles, listings, and seller information.

### Current Existing Features

- Email/password register, login, logout.
- Profile edit with photo, bio, faculty, and role.
- Listing browse, search, create, edit, detail.
- Order flow: `pending`, `in_progress`, `delivered`, `completed`, `paid`, `cancelled`, `rejected`.
- In-order chat, result upload, review/rating.
- Firestore security rules and indexes.

### MVP Modifications

- Add Google login as a simple login/register option.
- Add forgot password.
- Add clearer escrow simulation wording without changing the current order lifecycle.
- Add service policy acknowledgement to listing create/edit.
- Expose listing deactivation instead of hard delete in the client UI.

### New MVP Features

- Verification Center.
- Identity Verification using KTM only.
- Skill Verification for the three existing app categories.
- Public user portfolio.
- Public verification badges.
- Firebase Storage rules for private verification files.

---

## 2. Scope and Out of Scope

### In Scope

- Optional verification flow from Profile.
- Identity Verification using KTM upload.
- Skill Verification using portfolio, optional link, and optional evidence file.
- Public portfolio items with one optional image per item.
- Manual admin review through Firebase Console.
- Badge display on profile, user profile, listing card, listing detail, and seller information.
- Firestore and Storage rules for user/admin access boundaries.
- Google login via Firebase Auth.
- Forgot password for email/password accounts.
- Service policy acknowledgement when creating or editing listings.
- Listing deactivation for sellers.

### Out of Scope

- Admin panel inside the Android app.
- Cloud Functions automation.
- Custom in-app admin dashboard.
- KTP or other government identity documents.
- Real payment gateway.
- Automatic escrow.
- Order revision flow.
- Dispute and refund system.
- Push notifications.
- Multi-campus verification.
- Business account.
- Featured listing purchase.
- Account linking between email/password and Google login.

---

## 3. Actors and Permissions

| Actor | Permission Summary |
|---|---|
| Guest | Can access login/register only. |
| Authenticated User | Can browse listings, manage own profile, manage own listings, create orders, chat in own orders, submit reviews, submit verification. |
| Buyer | Can create orders, cancel pending orders, confirm delivered work, simulate payment, write one review after paid. |
| Seller | Can create/edit/deactivate listings, accept/reject orders, upload result files, submit skill verification. |
| Admin | Reviews verification submissions manually in Firebase Console using Firebase project access. No Android admin UI in MVP. |

---

## 4. Functional Requirements

### Auth

**AUTH-001 - Email/password auth remains supported.**  
Existing email/password register and login must continue to work with any valid email address.

Acceptance Criteria:
- AUTH-001-AC1: A valid email/password user can register, auto-login, and reach Home.
- AUTH-001-AC2: Login validation and error handling remain in Bahasa Indonesia.
- AUTH-001-AC3: No campus email restriction is reintroduced.

Impacted areas:
- Existing: `AuthRepository`, `LoginScreen`, `RegisterScreen`, `LoginViewModel`, `RegisterViewModel`.

**AUTH-002 - Google login/register.**  
Users can sign in with Google. If no `users/{uid}` profile exists after Firebase Auth succeeds, the app creates a minimal profile.

Acceptance Criteria:
- AUTH-002-AC1: LoginScreen shows a Google sign-in button.
- AUTH-002-AC2: A new Google auth user gets a `users/{uid}` profile with name, email, optional photoUrl, role `both`, and createdAt.
- AUTH-002-AC3: An existing Google auth user reuses the existing profile.
- AUTH-002-AC4: Account linking between email/password and Google accounts is not required in MVP.
- AUTH-002-AC5: Firebase Google provider setup is documented as an implementation dependency.

Impacted areas:
- Modify: `gradle/libs.versions.toml`, `app/build.gradle.kts`, `AuthRepository`, `LoginViewModel`, `LoginScreen`, `AppStrings`.
- Dependency: Android Credential Manager / Google ID integration with Firebase Auth.

**AUTH-003 - Forgot password.**  
Email/password users can request a password reset email.

Acceptance Criteria:
- AUTH-003-AC1: LoginScreen exposes "Lupa password?".
- AUTH-003-AC2: User enters email and receives success/error feedback.
- AUTH-003-AC3: Empty or invalid email is rejected client-side.
- AUTH-003-AC4: Copy clearly states that reset instructions are sent by email.

Impacted areas:
- Modify: `AuthRepository`, `LoginViewModel`, `LoginScreen`, `AppStrings`.

### Verification Center

**VER-001 - Profile entry point.**  
ProfileScreen must show an optional "Pusat Verifikasi" entry.

Acceptance Criteria:
- VER-001-AC1: Entry is visible for authenticated users.
- VER-001-AC2: Entry shows identity status and skill status summary.
- VER-001-AC3: Verification is optional; unverified users can still use the marketplace.

Impacted areas:
- Modify: `ProfileScreen`, `ProfileViewModel`, `Screen`, `NavGraph`, `AppStrings`.

**VER-002 - Verification Center dashboard.**  
The Verification Center shows two sections: Identity Verification and Skill Verification.

Acceptance Criteria:
- VER-002-AC1: Each section shows one of: belum diajukan, menunggu review, disetujui, ditolak.
- VER-002-AC2: Rejected sections show admin rejection reason when available.
- VER-002-AC3: Approved sections show the active public badge.
- VER-002-AC4: Pending sections prevent duplicate submission.

Impacted areas:
- New: `VerificationCenterScreen`, `VerificationCenterViewModel`, `VerificationRepository`.
- Modify: `Screen`, `NavGraph`, `AppStrings`.

**VER-003 - Status values.**  
Verification submissions use stable status constants.

Acceptance Criteria:
- VER-003-AC1: Supported values are `not_submitted`, `pending`, `approved`, `rejected`.
- VER-003-AC2: UI labels are Bahasa Indonesia.
- VER-003-AC3: Status constants live in `Constants.kt` or a verification-specific constants file.

Impacted areas:
- Modify: `Constants.kt`.

### Identity Verification

**IDV-001 - KTM-only submission.**  
Identity Verification accepts only KTM for MVP.

Acceptance Criteria:
- IDV-001-AC1: UI asks user to upload KTM.
- IDV-001-AC2: UI does not mention KTP or other documents as accepted MVP options.
- IDV-001-AC3: User can add an optional note if needed.
- IDV-001-AC4: Submission starts in `pending` status.

Impacted areas:
- New: `IdentityVerificationScreen`, `IdentityVerificationViewModel`.
- Modify: `StorageRepository`, `VerificationRepository`, `AppStrings`.

**IDV-002 - Identity resubmission.**  
Rejected users can resubmit KTM.

Acceptance Criteria:
- IDV-002-AC1: Rejected state shows rejection reason.
- IDV-002-AC2: User can submit a replacement KTM after rejection.
- IDV-002-AC3: Resubmission changes status back to `pending`.
- IDV-002-AC4: Pending and approved submissions cannot be overwritten by the user.

Impacted areas:
- New: `IdentityVerificationScreen`, `VerificationRepository`.
- Modify: Firestore rules, Storage rules.

**IDV-003 - Identity privacy.**  
KTM files are private.

Acceptance Criteria:
- IDV-003-AC1: KTM download URL is not stored in `users/{uid}` or any public listing/profile field.
- IDV-003-AC2: Submission stores a private `storagePath` and safe metadata only.
- IDV-003-AC3: Only the owner can upload/read their own pending/rejected KTM from the app.
- IDV-003-AC4: Admin can inspect files from Firebase Console.
- IDV-003-AC5: Other users cannot read KTM files.

Impacted areas:
- Modify: `firebase.json`.
- New: `storage.rules`.
- Modify: `StorageRepository`, Firestore rules.

### Skill Verification

**SKV-001 - Skill categories.**  
Skill Verification uses the three existing Berima categories.

Acceptance Criteria:
- SKV-001-AC1: Supported skill categories are `academic`, `visual`, `data`.
- SKV-001-AC2: Labels match existing category language: Akademik, Desain, Data.
- SKV-001-AC3: No broad PDD taxonomy is introduced in MVP.

Impacted areas:
- Reuse: `Category`, `CategoryVisuals`.
- New: `SkillVerificationScreen`, `SkillVerificationViewModel`.

**SKV-002 - Skill submission evidence.**  
Users submit skill verification using portfolio evidence.

Acceptance Criteria:
- SKV-002-AC1: User selects one skill category.
- SKV-002-AC2: User can attach an existing portfolio item.
- SKV-002-AC3: User can add an optional external link.
- SKV-002-AC4: User can upload one optional evidence file.
- SKV-002-AC5: Submission starts in `pending` status.

Impacted areas:
- New: `SkillVerificationScreen`, `VerificationRepository`.
- Modify: `StorageRepository`, Firestore rules, Storage rules.

**SKV-003 - Multiple skill badges.**  
One user can hold multiple approved skill badges, at most one per MVP category.

Acceptance Criteria:
- SKV-003-AC1: User can have approved badges for Akademik, Desain, and Data.
- SKV-003-AC2: Duplicate approved badge for the same category is not created.
- SKV-003-AC3: Rejected category can be resubmitted.

Impacted areas:
- Modify: `User` model public badge fields.
- New: `VerificationRepository`.

### Portfolio

**PORT-001 - Portfolio item CRUD.**  
Users can manage public portfolio items from Profile.

Acceptance Criteria:
- PORT-001-AC1: User can create portfolio item with title, description, category, optional link, optional image.
- PORT-001-AC2: User can edit and delete own portfolio item.
- PORT-001-AC3: Title and description have client-side length limits.
- PORT-001-AC4: Portfolio item copy is in Bahasa Indonesia.

Impacted areas:
- New: `PortfolioItem` model, `PortfolioRepository`, portfolio UI/ViewModel.
- Modify: `ProfileScreen`, `UserProfileScreen`, `Screen`, `NavGraph`, `AppStrings`.

**PORT-002 - Public portfolio display.**  
Portfolio items are visible on user profiles.

Acceptance Criteria:
- PORT-002-AC1: UserProfileScreen shows public portfolio items for the viewed seller.
- PORT-002-AC2: ProfileScreen shows own portfolio management entry.
- PORT-002-AC3: Portfolio image is rendered when available.
- PORT-002-AC4: External link opens through Android intent or is copied/displayed safely.

Impacted areas:
- Modify: `ProfileScreen`, `UserProfileScreen`.
- New: shared portfolio components if needed.

### Badges

**BADGE-001 - Public identity badge.**  
Approved identity verification creates a public identity badge.

Acceptance Criteria:
- BADGE-001-AC1: Badge appears on own Profile after admin approval.
- BADGE-001-AC2: Badge appears on UserProfile for other users.
- BADGE-001-AC3: Badge appears in ListingDetail seller information.
- BADGE-001-AC4: Badge does not expose KTM file or document metadata.

Impacted areas:
- Modify: `User`, `ProfileScreen`, `UserProfileScreen`, `ListingDetailScreen`.

**BADGE-002 - Public skill badges.**  
Approved skill verification creates public category badges.

Acceptance Criteria:
- BADGE-002-AC1: Skill badges appear on Profile and UserProfile.
- BADGE-002-AC2: ListingCard may show a verified skill badge only when the seller is verified for that listing category.
- BADGE-002-AC3: ListingDetail seller section shows relevant verified skill badge.
- BADGE-002-AC4: Skill badge labels are concise and fit mobile UI.

Impacted areas:
- Modify: `User`, `Listing`, `ListingCard`, `ListingDetailScreen`, `UserProfileScreen`.

**BADGE-003 - Manual badge synchronization.**  
Because MVP has no Cloud Functions, admin manually updates submission status and public user badge fields in Firebase Console.

Acceptance Criteria:
- BADGE-003-AC1: PRD documents the required admin checklist.
- BADGE-003-AC2: App tolerates mismatch by treating public user badge fields as the display source.
- BADGE-003-AC3: Verification Center still shows submission status from submission documents.

Impacted areas:
- Firestore schema and admin process documentation.

### Listing and Service Policy

**LST-001 - Service policy acknowledgement.**  
Create/Edit Listing must require acknowledgement that the service follows Berima policy.

Acceptance Criteria:
- LST-001-AC1: CreateListing cannot submit until policy acknowledgement is checked.
- LST-001-AC2: EditListing requires acknowledgement before saving changes.
- LST-001-AC3: Policy copy prohibits joki tugas, joki ujian, plagiarism, thesis writing on behalf of others, document falsification, and illegal activity.
- LST-001-AC4: Copy is concise and in Bahasa Indonesia.

Impacted areas:
- Modify: `ListingFormContent`, `CreateListingViewModel`, `EditListingViewModel`, `AppStrings`.

**LST-002 - Deactivate listing.**  
Sellers can deactivate their own listing instead of hard deleting it from the client UI.

Acceptance Criteria:
- LST-002-AC1: Owner can deactivate active listing.
- LST-002-AC2: Deactivated listing is hidden from Home/Search/UserProfile public listing sections.
- LST-002-AC3: Own Profile can still show inactive listings with an inactive label or separate state.
- LST-002-AC4: Hard delete is not exposed in MVP UI.

Impacted areas:
- Existing method: `ListingRepository.setListingActive`.
- Modify: `ProfileScreen`, `ListingDetailScreen`, `EditListingScreen`, `AppStrings`.

### Order and Escrow Simulation

**ORD-001 - Keep current order lifecycle.**  
MVP keeps the current implemented order lifecycle.

Acceptance Criteria:
- ORD-001-AC1: No new revision status is added for MVP.
- ORD-001-AC2: Existing statuses remain stable.
- ORD-001-AC3: Existing review trigger remains after `paid`.

Impacted areas:
- No data model change required.

**ORD-002 - Clarify escrow simulation.**  
Order detail copy must explain payment/escrow as a simulation.

Acceptance Criteria:
- ORD-002-AC1: "Simulasi Bayar" remains the action after completed.
- ORD-002-AC2: UI explains that no real money is processed in MVP.
- ORD-002-AC3: Demo copy can describe Midtrans/payment gateway as future placeholder.

Impacted areas:
- Modify: `OrderActions`, `OrderDetailScreen`, `AppStrings`.

---

## 5. Data Model Requirements

### User Additions

Add public fields to `users/{uid}`:

```kotlin
identityVerificationStatus: String // "not_submitted" | "pending" | "approved" | "rejected"
isIdentityVerified: Boolean
verifiedSkillBadges: List<String> // category ids: "academic", "visual", "data"
verificationUpdatedAt: Timestamp?
```

Rules:
- User may read these fields.
- User must not be able to self-set `isIdentityVerified` or `verifiedSkillBadges`.
- Admin manually updates these fields in Firebase Console after review.

Impacted file:
- `app/src/main/java/upnvj/berima/v1/data/model/User.kt`

### Listing Additions

Optionally denormalize seller badges onto listings for cheaper list rendering:

```kotlin
sellerIdentityVerified: Boolean
sellerVerifiedSkillBadges: List<String>
policyAcceptedAt: Timestamp?
```

Rules:
- `policyAcceptedAt` is written by seller create/edit flow.
- Badge denormalization may be set at create/update time from current seller user fields.
- Public badge source of truth remains `users/{uid}`.

Impacted file:
- `app/src/main/java/upnvj/berima/v1/data/model/Listing.kt`

### VerificationSubmission

New collection: `verificationSubmissions/{submissionId}`

```kotlin
submissionId: String
userId: String
type: String // "identity" | "skill"
status: String // "pending" | "approved" | "rejected"
documentType: String? // identity: "ktm"
skillCategory: String? // skill: "academic" | "visual" | "data"
portfolioItemId: String?
externalLink: String?
storagePath: String?
fileName: String?
contentType: String?
note: String?
rejectionReason: String?
reviewedBy: String?
reviewedAt: Timestamp?
createdAt: Timestamp
updatedAt: Timestamp
```

Rules:
- Users create submissions for themselves.
- Users can read their own submissions.
- Users can resubmit only after `rejected`.
- Users cannot write approval/rejection/admin fields.
- Admin edits status and admin fields in Firebase Console.

### PortfolioItem

New collection: `portfolioItems/{portfolioItemId}`

```kotlin
portfolioItemId: String
userId: String
title: String
description: String
category: String // "academic" | "visual" | "data"
externalLink: String?
imageUrl: String?
imageStoragePath: String?
createdAt: Timestamp
updatedAt: Timestamp
```

Rules:
- Authenticated users can read portfolio items.
- Owners can create/update/delete their own portfolio items.
- Portfolio images are public-readable to authenticated users.

### Required Indexes

Add indexes if queries require ordering:

| Collection | Fields |
|---|---|
| `verificationSubmissions` | `userId ASC`, `createdAt DESC` |
| `verificationSubmissions` | `userId ASC`, `type ASC`, `createdAt DESC` |
| `portfolioItems` | `userId ASC`, `createdAt DESC` |
| `portfolioItems` | `userId ASC`, `category ASC`, `createdAt DESC` |

---

## 6. Firebase Storage Requirements

### Storage Paths

```text
users/{userId}/verification/identity/{submissionId}/{filename}
users/{userId}/verification/skill/{submissionId}/{filename}
users/{userId}/portfolio/{portfolioItemId}/{filename}
orders/{orderId}/result/{filename}       // existing
users/{userId}/profile/{filename}        // existing
```

### Storage Rules

**STO-001 - Add Storage rules config.**

Acceptance Criteria:
- STO-001-AC1: `firebase.json` includes Storage rules configuration.
- STO-001-AC2: `storage.rules` exists and covers verification, portfolio, profile, and order result files.

**STO-002 - Private identity and skill evidence.**

Acceptance Criteria:
- STO-002-AC1: Only the owner can read/write their verification files from the app.
- STO-002-AC2: Other authenticated users cannot read identity or skill evidence files.
- STO-002-AC3: Verification file paths are not displayed publicly.

**STO-003 - Portfolio images.**

Acceptance Criteria:
- STO-003-AC1: Owner can upload portfolio image.
- STO-003-AC2: Authenticated users can read portfolio images.
- STO-003-AC3: Image size/type constraints are enforced client-side and documented for rules where practical.

---

## 7. Security Rules Requirements

**SEC-001 - Protect verification public fields.**

Acceptance Criteria:
- SEC-001-AC1: User cannot update `isIdentityVerified`.
- SEC-001-AC2: User cannot update `verifiedSkillBadges`.
- SEC-001-AC3: User cannot update admin review fields.

**SEC-002 - Submission access.**

Acceptance Criteria:
- SEC-002-AC1: User can create submission only where `request.resource.data.userId == request.auth.uid`.
- SEC-002-AC2: User can read only own submissions.
- SEC-002-AC3: User cannot approve or reject own submission.
- SEC-002-AC4: Admin review is performed with Firebase Console/admin privileges, not client rules.

**SEC-003 - Portfolio access.**

Acceptance Criteria:
- SEC-003-AC1: Authenticated users can read portfolio items.
- SEC-003-AC2: Only owner can create/update/delete own portfolio items.

**SEC-004 - Listing policy and deactivation.**

Acceptance Criteria:
- SEC-004-AC1: Existing seller ownership protections remain.
- SEC-004-AC2: Seller cannot change `sellerId`.
- SEC-004-AC3: Seller can set own `isActive` false.

---

## 8. Screen Requirements

### LoginScreen

- Add Google login button.
- Add forgot password affordance.
- Preserve current editorial layout and design system.

### ProfileScreen

- Add Verification Center entry.
- Add Portfolio management entry/section.
- Show identity and skill badges when approved.
- Show inactive listing state if listing deactivation is added.

### VerificationCenterScreen

- Two primary sections: Identitas and Keahlian.
- Show status summary, rejection reason, and CTA.
- CTAs: Ajukan Verifikasi, Lihat Status, Ajukan Ulang.

### IdentityVerificationScreen

- Upload KTM.
- Optional note.
- Submit and status state.
- Rejection and resubmit state.

### SkillVerificationScreen

- Choose skill category.
- Select existing portfolio item or provide evidence.
- Optional external link.
- Optional evidence file upload.
- Rejection and resubmit state.

### Portfolio Screens/Sections

- Add/edit portfolio item.
- List own portfolio on Profile.
- Display public portfolio on UserProfile.

### ListingCard and ListingDetailScreen

- Show seller verified badges where relevant.
- Skill badge shown only if seller has badge for listing category.

---

## 9. Error States and Edge Cases

- Google login cancelled by user.
- Google login succeeds but profile creation fails.
- Forgot password email invalid or not found.
- Verification upload file too large.
- Verification upload fails midway.
- User leaves verification screen after upload but before Firestore submission.
- Submission pending and user tries to submit again.
- Submission rejected without reason due to admin omission.
- Admin updates submission status but forgets public user badge field.
- Portfolio image upload succeeds but item creation fails.
- Listing deactivation attempted by non-owner.
- Existing seeded users do not have new verification fields.

Fallback behavior:
- Missing verification fields default to not submitted/unverified.
- Missing badge fields default to empty/false.
- Missing rejection reason displays generic rejection copy.

---

## 10. Testing Requirements

### Unit and ViewModel

- Google login success creates profile if absent.
- Forgot password validation.
- Verification Center status mapping.
- Identity submission validation.
- Skill submission validation.
- Portfolio create/edit/delete validation.
- Listing policy acknowledgement required.

### Firebase Rules

- User cannot self-approve identity.
- User cannot write own public badge fields.
- User can create own submission.
- User cannot read another user's KTM submission file.
- Authenticated user can read public portfolio item.
- Non-owner cannot update portfolio item.

### Maestro / Manual QA

- Login with email/password still works.
- Login with Google reaches Home.
- Forgot password flow shows success state.
- Profile opens Verification Center.
- Identity verification pending/rejected/approved states render.
- Skill verification pending/rejected/approved states render.
- Badges appear on Profile, UserProfile, ListingCard, ListingDetail.
- Public portfolio displays on UserProfile.
- Deactivated listing disappears from Home/Search.

---

## 11. Implementation Priority

### P0 - Security and Schema Foundation

- Add constants and models.
- Add Firestore rules for protected fields and new collections.
- Add Storage rules and `firebase.json` config.
- Add indexes.

### P1 - Auth and Profile Entry Points

- Google login.
- Forgot password.
- Profile Verification Center entry.
- Navigation routes.

### P2 - Verification Flows

- Verification Center dashboard.
- Identity Verification.
- Skill Verification.
- Admin manual review checklist in documentation.

### P3 - Portfolio and Badges

- Portfolio CRUD and public display.
- Badge components and placement.
- Listing/Profile/UserProfile integration.

### P4 - Listing and Order Polish

- Service policy acknowledgement.
- Listing deactivation UI.
- Escrow simulation copy.

---

## 12. Admin Manual Review Process

Admin uses Firebase Console.

### Approve Identity

1. Open `verificationSubmissions/{submissionId}`.
2. Confirm `type == "identity"`, `documentType == "ktm"`, and inspect storage file.
3. Set submission `status = "approved"`, `reviewedAt`, `reviewedBy`.
4. Update `users/{userId}`:
   - `identityVerificationStatus = "approved"`
   - `isIdentityVerified = true`
   - `verificationUpdatedAt = now`

### Reject Identity

1. Set submission `status = "rejected"`.
2. Set `rejectionReason`.
3. Set `reviewedAt`, `reviewedBy`.
4. Update `users/{userId}.identityVerificationStatus = "rejected"`.
5. Do not set `isIdentityVerified = true`.

### Approve Skill

1. Open skill submission.
2. Inspect selected portfolio item, link, and optional evidence file.
3. Set submission `status = "approved"`.
4. Add the category id to `users/{userId}.verifiedSkillBadges`.
5. Set `verificationUpdatedAt = now`.

### Reject Skill

1. Set submission `status = "rejected"`.
2. Set `rejectionReason`.
3. Do not add badge to `verifiedSkillBadges`.

---

## 13. Decision Log

| ID | Decision | Reason |
|---|---|---|
| DEC-PRD-001 | Verification Center is implementable MVP scope. | Trust is a core product differentiator and can be added without admin panel. |
| DEC-PRD-002 | Verification is optional. | Keeps onboarding low-friction. |
| DEC-PRD-003 | Identity Verification accepts KTM only. | Reduces privacy risk and scope. |
| DEC-PRD-004 | Admin review is Firebase Console-only. | Matches MVP constraints and avoids admin app complexity. |
| DEC-PRD-005 | Public badge fields are manually synced by admin. | Avoids Cloud Functions for MVP. |
| DEC-PRD-006 | Skill Verification uses existing categories only. | Keeps taxonomy aligned with current listing model. |
| DEC-PRD-007 | Portfolio supports link plus one optional image. | Gives sellers useful proof without multi-file complexity. |
| DEC-PRD-008 | Google login is simple login/register only. | Useful UX improvement with manageable scope. |
| DEC-PRD-009 | Order revision is deferred. | Avoids large lifecycle changes to existing order flow. |
| DEC-PRD-010 | Escrow simulation keeps current flow with clearer copy. | Preserves working demo flow while being honest about MVP payment. |
| DEC-PRD-011 | Forgot password and service policy are MVP scope. | Low/moderate complexity and important for usability/integrity. |

---

## 14. PDD Items Not Followed

| PDD Item | PRD Decision | Reason |
|---|---|---|
| Target UPN-only users | Keep any valid email. | Current app already removed campus email restriction; supports broader demo. |
| KTP and other identity docs | KTM only. | Privacy and MVP scope control. |
| Full admin role/app | Firebase Console only. | No admin panel in MVP. |
| Full escrow lifecycle before work starts | Keep current simulated payment after completion. | Current order flow is already built and demo-ready. |
| Order revision max one time | Deferred. | Requires status/actions/rules/test changes beyond current MVP priority. |
| Dispute and refund | Out of scope. | PDD itself treats these as later maturity features. |
| Broad skill taxonomy | Use existing 3 categories. | Avoids adding taxonomy not present in current UI/data model. |
| Featured listing monetization | Out of scope. | Current "Sedang ramai" is popularity rail, not paid placement. |
| Automatic badge sync | Manual admin sync. | No Cloud Functions in current architecture. |

---

## 15. Open Questions

No blocking product questions remain for this PRD version.

Future discussion items:
- Whether to add Cloud Functions for badge synchronization.
- Whether to add admin web/app panel.
- Whether to support multiple portfolio files.
- Whether to introduce order revision in a later release.
- Whether verification should expire and require periodic renewal.
