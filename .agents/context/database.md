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
averageRating:        Double      // default 0.0
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

## Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }

    match /listings/{listingId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null
        && request.auth.uid == resource.data.sellerId;
    }

    match /orders/{orderId} {
      allow read: if request.auth != null
        && (request.auth.uid == resource.data.buyerId
        || request.auth.uid == resource.data.sellerId);
      allow create: if request.auth != null;
      allow update: if request.auth != null
        && (request.auth.uid == resource.data.buyerId
        || request.auth.uid == resource.data.sellerId);
    }

    match /reviews/{reviewId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null
        && request.auth.uid == request.resource.data.buyerId;
    }

    match /messages/{orderId}/chats/{messageId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

---

## Required Composite Indexes

Create these in Firebase Console → Firestore → Indexes:

| Collection | Fields | Order |
|---|---|---|
| `listings` | `isActive` ASC, `createdAt` DESC | — |
| `listings` | `category` ASC, `createdAt` DESC | — |
| `listings` | `sellerId` ASC, `isActive` ASC | — |
| `orders` | `buyerId` ASC, `createdAt` DESC | — |
| `orders` | `sellerId` ASC, `createdAt` DESC | — |
| `reviews` | `sellerId` ASC, `createdAt` DESC | — |
| `reviews` | `listingId` ASC, `createdAt` DESC | — |
