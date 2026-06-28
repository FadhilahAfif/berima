# Database Schema (Firestore)

## Collections

---

### `users/{userId}`
`userId` = Firebase Auth UID. Created on register.

```
uid:                  String      // same as document ID
name:                 String
email:                String      // any valid email address
photoUrl:             String?     // Firebase Storage URL
bio:                  String?     // max 150 chars
faculty:              String?
role:                 String      // "buyer" | "seller" | "both"
identityVerificationStatus: String // "not_submitted" | "pending" | "approved" | "rejected"
isIdentityVerified:  Boolean     // public badge flag; admin-managed
verifiedSkillBadges: List<String> // category IDs: "academic" | "visual" | "data"; admin-managed
verificationUpdatedAt: Timestamp?
averageRating:        Double      // default 0.0
totalReviews:         Int         // default 0
totalOrdersAsBuyer:   Int         // default 0
totalOrdersAsSeller:  Int         // default 0
createdAt:            Timestamp
```

---

### `listings/{listingId}`

```
listingId:            String      // same as document ID
sellerId:             String
sellerName:           String      // denormalized
sellerPhotoUrl:       String?     // denormalized
sellerRating:         Double      // denormalized
title:                String      // max 60 chars
description:          String      // max 500 chars
category:             String      // "academic" | "visual" | "data"
price:                Long        // in Rupiah, use Long not Double
deliveryTimeHours:    Int         // max 48
thumbnailUrl:         String?     // Firebase Storage URL
tags:                 List<String>
isActive:             Boolean     // default true
sellerIdentityVerified: Boolean   // denormalized display helper
sellerVerifiedSkillBadges: List<String> // denormalized display helper
policyAcceptedAt:     Timestamp?  // set when seller accepts service policy
averageRating:        Double      // default 0.0
reviewCount:          Int         // default 0; used as rating divisor
totalOrders:          Int         // default 0
createdAt:            Timestamp
```

---

### `orders/{orderId}`

```
orderId:              String      // same as document ID
listingId:            String
listingTitle:         String      // denormalized
buyerId:              String
buyerName:            String      // denormalized
sellerId:             String
sellerName:           String      // denormalized
price:                Long        // price at time of order, immutable after creation
note:                 String?     // buyer's note, max 300 chars
status:               String      // see status values below
attachmentUrl:        String?     // seller's result file, Firebase Storage URL
hasReview:            Boolean     // default false
createdAt:            Timestamp
updatedAt:            Timestamp
```

**Status values:**
```
"pending"       buyer placed order, waiting for seller
"in_progress"   seller accepted, working on it
"delivered"     seller uploaded result, waiting for buyer confirmation
"completed"     buyer confirmed result received
"paid"          payment confirmed (MVP: simulated)
"cancelled"     buyer cancelled while still pending
"rejected"      seller rejected the order
```

---

### `reviews/{reviewId}`

```
reviewId:         String      // same as document ID
orderId:          String
listingId:        String
buyerId:          String
buyerName:        String      // denormalized
buyerPhotoUrl:    String?     // denormalized
sellerId:         String
rating:           Int         // 1–5
comment:          String?     // max 300 chars
createdAt:        Timestamp
```

---

### `messages/{orderId}/chats/{messageId}`
Subcollection under each order document.

```
messageId:    String      // same as document ID
senderId:     String
senderName:   String      // denormalized
text:         String      // max 500 chars
isRead:       Boolean     // default false
createdAt:    Timestamp
```

---

### `verificationSubmissions/{submissionId}`

Manual-review verification submissions for Identity Verification and Skill Verification.

```
submissionId:    String      // same as document ID
userId:          String      // owner UID
type:            String      // "identity" | "skill"
status:          String      // "pending" | "approved" | "rejected"
documentType:    String?     // identity only: "ktm"
skillCategory:   String?     // skill only: "academic" | "visual" | "data"
portfolioItemId: String?     // optional supporting portfolio item
externalLink:    String?     // optional supporting link
storagePath:     String?     // private Storage path, never a public KTM URL
fileName:        String?
contentType:     String?
note:            String?
rejectionReason: String?
reviewedBy:      String?     // admin-entered in Firebase Console
reviewedAt:      Timestamp?
createdAt:       Timestamp
updatedAt:       Timestamp
```

Rules:
- Users can create/read their own submissions.
- Users cannot set `approved`, `rejected`, `reviewedBy`, `reviewedAt`, or public badge fields from the app.
- Pending and approved submissions cannot be overwritten by the user.
- Rejected submissions can be resubmitted by creating/updating a new pending submission.
- Admin review is manual through Firebase Console.

---

### `portfolioItems/{portfolioItemId}`

Public portfolio items displayed on user profiles and optionally used as skill verification evidence.

```
portfolioItemId: String      // same as document ID
userId:          String      // owner UID
title:           String
description:     String
category:        String      // "academic" | "visual" | "data"
externalLink:    String?
imageUrl:        String?     // readable by authenticated users
imageStoragePath:String?
createdAt:       Timestamp
updatedAt:       Timestamp
```

Rules:
- Authenticated users can read portfolio items.
- Owners can create/update/delete their own portfolio items.
- One optional image per portfolio item in MVP.

---

## Firestore Security Rules

`firestore.rules` is the source file to deploy. Keep it stricter than this document's summaries.

Required behavior:
- Authenticated users can read user/listing/review public data.
- Users can update their own editable profile fields only.
- Users cannot update their own `isIdentityVerified`, `verifiedSkillBadges`, admin review fields, or any public badge fields.
- Listing create/update must preserve `sellerId == request.auth.uid`; owners can deactivate via `isActive = false`.
- Order create/update must preserve buyer, seller, listing, and price invariants already enforced in `firestore.rules`.
- Chat read/write remains limited to the order buyer and seller.
- Verification submissions are readable/writable only by their owner from the app, with admin-only fields protected from client writes.
- Portfolio items are readable by authenticated users and writable only by their owner.

---

## Firebase Storage Schema and Rules

`firebase.json` must include Storage rules before identity/skill evidence upload is implemented.

Storage paths:

```
users/{userId}/verification/identity/{submissionId}/{filename}
users/{userId}/verification/skill/{submissionId}/{filename}
users/{userId}/portfolio/{portfolioItemId}/{filename}
users/{userId}/listings/{listingId}/{filename}
users/{userId}/profile/{filename}
orders/{orderId}/result/{filename}
```

Required behavior:
- KTM files under `verification/identity` are private. Never expose public download URLs in user/listing/profile data.
- Skill evidence files under `verification/skill` are private to the owner from the app.
- Admin inspection happens through Firebase Console project access.
- Portfolio images can be readable by authenticated users.
- Listing thumbnail images can be readable by authenticated users and writable only by the listing owner.
- Profile photos remain readable where needed for profile/listing display.
- Order result files must be accessible only to the order buyer and seller.

## Required Composite Indexes

Create or deploy these via `firestore.indexes.json`:

| Collection | Fields | Order |
|---|---|---|
| `listings` | `isActive` ASC, `createdAt` DESC | — |
| `listings` | `isActive` ASC, `totalOrders` DESC | — |
| `listings` | `category` ASC, `createdAt` DESC | — |
| `listings` | `sellerId` ASC, `isActive` ASC | — |
| `orders` | `buyerId` ASC, `createdAt` DESC | — |
| `orders` | `sellerId` ASC, `createdAt` DESC | — |
| `reviews` | `sellerId` ASC, `createdAt` DESC | — |
| `reviews` | `listingId` ASC, `createdAt` DESC | — |
| `verificationSubmissions` | `userId` ASC, `createdAt` DESC | PRD P0 |
| `verificationSubmissions` | `userId` ASC, `type` ASC, `createdAt` DESC | PRD P0 |
| `portfolioItems` | `userId` ASC, `createdAt` DESC | PRD P0 |
| `portfolioItems` | `userId` ASC, `category` ASC, `createdAt` DESC | PRD P0 |
