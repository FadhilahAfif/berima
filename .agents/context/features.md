# Feature Specification

## Navigation Structure

```
Splash
├── [not logged in] → Login → Register
└── [logged in] → Main
      ├── Tab: Home
      │     ├── ListingDetail
      │     │     └── CreateOrder → OrderDetail
      │     └── UserProfile → ListingDetail
      ├── Tab: Orders
      │     └── OrderDetail → CreateReview
      └── Tab: Profile
            ├── EditProfile
            ├── CreateListing
            ├── EditListing
            └── ListingDetail (own listing)
```

Bottom navigation items: **Home** (browse) | **Orders** | **Profile**

---

## Screen Specifications

### SplashScreen
- Show Berima logo and tagline
- Check Firebase Auth state
- Navigate to Home if logged in, Login if not

---

### LoginScreen
- Fields: email, password
- Validation: email must match `@upnvj.ac.id` domain
- Specific error messages: "Email tidak terdaftar" / "Password salah"
- Link to RegisterScreen

### RegisterScreen
- Fields: full name, email, password, confirm password
- Validation: email domain `@upnvj.ac.id`, password min 8 chars, passwords must match
- On success: auto-login → navigate to Home
- No email verification step (MVP simplification)

---

### HomeScreen
- Search bar at top (navigates to search mode on tap)
- Category filter chips: Semua | Academic Support | Visual Branding | Data Processing
- Listing grid or list: ListingCard with thumbnail, title, price, seller name, seller rating
- Pull-to-refresh
- Paginate: load 10 items, load more on scroll to bottom
- Query: `listings` where `isActive == true`, ordered by `createdAt` DESC

### SearchScreen
- Activated when user taps search bar on Home
- Client-side filter on listing title as user types
- Show filtered results in real time

---

### ListingDetailScreen
- Full-width thumbnail
- Title, price (Rupiah format), delivery time estimate
- Full description
- Seller card: photo, name, average rating, total completed orders
- Tags
- Reviews section: show latest 5, "Lihat semua" button
- **"Pesan Sekarang" button** — visible only if current user is NOT the listing owner
- **"Edit Listing" button** — visible only if current user IS the listing owner

### CreateListingScreen / EditListingScreen
- Required fields: title (max 60 chars), category (dropdown), description (max 500 chars),
  price (Rp), delivery time in hours (max 48)
- Optional: thumbnail (from gallery), tags
- Validate all required fields before submit
- On save: write to Firestore, navigate back to Profile

---

### CreateOrderScreen
- Show listing summary (title, seller, price)
- Optional field: note for seller (max 300 chars)
- Show total price
- "Konfirmasi Pesanan" button → create order document with status `pending`
- Navigate to OrderDetailScreen on success

### OrdersScreen
- Sub-tabs: "Sebagai Pembeli" | "Sebagai Penjual"
- List of orders with color-coded status chip
- Sorted by `createdAt` DESC
- Tap → OrderDetailScreen

### OrderDetailScreen

Order status flow:
```
pending → in_progress → delivered → completed → paid
```
Additional statuses: `cancelled` (buyer cancels at pending), `rejected` (seller rejects at pending)

Actions per role and status:

| Status | Buyer Actions | Seller Actions |
|---|---|---|
| `pending` | Cancel order | Accept / Reject |
| `in_progress` | — | Upload result file |
| `delivered` | Confirm done | — |
| `completed` | Simulate payment | — |
| `paid` | Write review (if not yet) | — |
| `cancelled` / `rejected` | — | — |

Below the status section: Chat section (see below)

**"Simulasi Bayar"** — MVP only. Changes status to `paid` immediately. No real payment.
Explain this as a Midtrans placeholder during demo.

### Chat Section (inside OrderDetailScreen)
- Real-time message list between buyer and seller for this specific order
- Text input + send button
- Text only, max 500 chars per message
- No media attachments in chat (result file is uploaded separately via order action)

---

### CreateReviewScreen
- Accessible from OrderDetailScreen after status is `paid` and `hasReview == false`
- One review per order, cannot be edited after submission
- Fields: star rating 1–5 (required), comment (optional, max 300 chars)
- On submit: create review document, update `averageRating` on listing and seller profile

---

### ProfileScreen (own)
- Profile photo (tappable to change)
- Name, email, bio, faculty
- Role badge: Pembeli / Penjual / Keduanya
- Stats: average rating (if seller), total orders as buyer, total orders as seller
- "Listing Saya" section with own listings
- "Tambah Listing Baru" button
- Edit Profile button
- Logout button

### EditProfileScreen
- Fields: name, bio (max 150 chars), faculty
- Role selector: Pembeli saja | Penjual saja | Keduanya
- Upload / change profile photo

### UserProfileScreen (other user, read-only)
- Photo, name, bio, rating, stats
- Active listings owned by this user

---

## Features NOT in MVP

| Feature | Reason |
|---|---|
| Real payment gateway (Midtrans) | Needs verified business account + significant integration time |
| Push notifications | FCM setup complexity |
| KTM photo verification | Needs OCR or manual review |
| Admin panel | Use Firebase Console directly |
| Dispute / complaint system | Handle manually during MVP |
| Multiple listing photos | One thumbnail is enough for concept validation |
| iOS support | Android first |
| Fund withdrawal | No real money in MVP |
