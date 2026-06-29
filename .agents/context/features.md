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
            ├── VerificationCenter
            │     ├── IdentityVerification
            │     └── SkillVerification
            ├── Portfolio management
            ├── CreateListing
            ├── EditListing
            └── ListingDetail (own listing)
```

Bottom navigation items: **Home** (browse) | **Orders** | **Profile**

Terminology note: user-facing copy should use `layanan`, `pemesan`, and `penyedia jasa`. Internal route names, model names, and Firestore field names may still use `Listing`, `buyer`, and `seller` when the technical contract depends on them.

---

## Screen Specifications

### SplashScreen
- Show Berima logo and tagline
- Check Firebase Auth state
- Navigate to Home if logged in, Login if not

---

### LoginScreen
- Fields: email, password
- Validation: valid email format
- Specific error messages: "Email tidak terdaftar" / "Password salah"
- Link/action for forgot password
- Google login button (simple login/register; no account linking required in MVP)
- Link to RegisterScreen

### RegisterScreen
- Fields: full name, email, password, confirm password
- Validation: valid email format, password min 8 chars, passwords must match
- On success: auto-login → navigate to Home
- No email verification step (MVP simplification)
- Google account creation happens through LoginScreen's Google login flow by creating a minimal profile when missing

### ForgotPassword flow
- Accessible from LoginScreen
- Requires a valid email address
- Sends Firebase Auth password reset email
- Shows Bahasa Indonesia success/error feedback

---

### HomeScreen
- Search bar at top (tappable, navigates to SearchScreen)
- **"Sedang ramai" horizontal rail**: top 5 listings ordered by `totalOrders DESC`, shown as a `LazyRow` of `ListingCard` (width 180dp). Hidden when empty.
- Category filter chips: Semua | Academic Support | Visual Branding | Data Processing
- **"Terbaru" vertical list**: active listings ordered by `createdAt DESC`, filtered by selected category, limit 20
- Empty state when no listings match the selected category
- Query for featured: `listings` where `isActive == true`, ordered by `totalOrders` DESC, limit 5
- Query for main list: `listings` where `isActive == true` (+ optional `category ==`), ordered by `createdAt` DESC, limit 20

### SearchScreen
- Activated when user taps search bar on Home
- Client-side filter on listing title as user types
- Show filtered results in real time

---

### ListingDetailScreen
- Full-width thumbnail, using the uploaded listing image when available or the generated category fallback image when `thumbnailUrl` is null
- Title, price (Rupiah format), delivery time estimate
- Full description
- Penyedia jasa card: photo, name, average rating, total completed orders, identity badge when approved, and relevant skill badge when approved for the listing category
- Tags
- Reviews section: show latest 5, "Lihat semua" button
- **"Pesan Sekarang" button** — visible only if current user is NOT the listing owner
- **"Edit Listing" button** — visible only if current user IS the listing owner

### CreateListingScreen / EditListingScreen
- Required fields: title (max 60 chars), category (dropdown), description (max 500 chars),
  price (Rp), delivery time in hours (max 48)
- Optional: one thumbnail image from gallery, tags
- Thumbnail images upload to Firebase Storage and are stored publicly as `thumbnailUrl` on the listing; old listings without a thumbnail use generated category fallback images in the UI
- Required service policy acknowledgement before submit/save
- Validate all required fields before submit
- On save: write to Firestore, navigate back to Profile
- Owner can deactivate an active listing from Profile, ListingDetail, or EditListing
- Do not expose hard delete in MVP client UI

---

### CreateOrderScreen
- Show listing summary (title, penyedia jasa, price)
- Optional field: note for penyedia jasa (max 300 chars)
- Show total price
- "Konfirmasi Pesanan" button → create order document with status `pending`
- Navigate to OrderDetailScreen on success

### OrdersScreen
- Sub-tabs: "Sebagai Pemesan" | "Sebagai Penyedia"
- List of orders with color-coded status chip
- Sorted by `createdAt` DESC
- Tap → OrderDetailScreen

### OrderDetailScreen

Order status flow:
```
pending → in_progress → delivered → completed → paid
```
Additional statuses: `cancelled` (pemesan cancels at pending), `rejected` (penyedia jasa rejects at pending)

Actions per role and status:

| Status | Pemesan Actions | Penyedia Jasa Actions |
|---|---|---|
| `pending` | Batalkan pesanan | Terima / Tolak |
| `in_progress` | — | Unggah hasil |
| `delivered` | Konfirmasi selesai | — |
| `completed` | Simulasi bayar | — |
| `paid` | Tulis ulasan (jika belum) | — |
| `cancelled` / `rejected` | — | — |

Below the status section: Chat section (see below)

**"Simulasi Bayar"** — MVP only. Changes status to `paid` immediately. No real payment.
Explain this as a Midtrans placeholder during demo.
Order revision is not part of the current PRD scope. Do not add revision statuses unless explicitly requested.

### Chat Section (inside OrderDetailScreen)
- Real-time message list between pemesan and penyedia jasa for this specific order
- Text input + send button
- Text only, max 500 chars per message
- No media attachments in chat (result file is uploaded separately via order action)

---

### CreateReviewScreen
- Accessible from OrderDetailScreen after status is `paid` and `hasReview == false`
- One review per order, cannot be edited after submission
- Fields: star rating 1–5 (required), comment (optional, max 300 chars)
- On submit: create review document, update `averageRating` on listing and penyedia jasa profile

---

### ProfileScreen (own)
- Profile photo (tappable to change)
- Name, email, bio, faculty
- Role badge: Pemesan / Penyedia Jasa / Keduanya
- Identity and skill verification badges when approved
- Stats: average rating (if penyedia jasa), total orders as pemesan, total orders as penyedia jasa
- "Pusat Verifikasi" entry with current identity/skill status summary
- Portfolio preview section with "Kelola" entry to Portfolio management
- "Layanan Saya" section with own layanan
- Inactive own listings remain visible here with a `NONAKTIF` label
- "Tambah Layanan Baru" button
- Edit Profile button
- Logout button

### EditProfileScreen
- Fields: name, bio (max 150 chars), faculty
- Role selector: Pemesan saja | Penyedia Jasa saja | Keduanya
- Upload / change profile photo

### UserProfileScreen (other user, read-only)
- Photo, name, bio, rating, stats, identity badge, skill badges
- Active layanan owned by this user
- Public portfolio items owned by this user

---

### VerificationCenterScreen
- Accessible from ProfileScreen
- Shows two sections: Verifikasi Identitas and Verifikasi Keahlian
- Each section shows status: belum diajukan, menunggu review, disetujui, ditolak
- Rejected submissions show rejection reason when provided by admin
- Pending submissions prevent duplicate submission
- Verification is optional and never blocks marketplace access

### IdentityVerificationScreen
- Accepts KTM only for MVP
- Uploads KTM to private Firebase Storage path
- Stores private `storagePath` and metadata in Firestore; never stores KTM download URL in public user/listing fields
- Optional note field
- Status flow: not submitted → pending → approved/rejected
- Rejected users can resubmit and return status to pending
- Admin review is manual through Firebase Console

### SkillVerificationScreen
- Uses existing categories only: Akademik (`academic`), Desain (`visual`), Data (`data`)
- User selects one category
- User can attach an existing portfolio item, add an optional external link, and upload one optional evidence file
- One approved skill badge per category
- Admin review is manual through Firebase Console

### Portfolio
- Users can create/edit/delete own portfolio items
- Fields: title, description, category, optional external link, optional single image
- Portfolio management is a single Profile-owned screen for create/edit/delete
- ProfileScreen shows the owner's latest portfolio preview and UserProfileScreen shows public portfolio items
- External portfolio links open through Android's URI handler when tapped
- Portfolio images may use readable download URLs; identity/skill evidence must not expose public URLs

### Badges
- Identity and skill badges use reusable UI from `ui/common/VerificationBadges.kt`
- Profile and UserProfile show approved identity and all approved skill badges from public `users/{uid}` fields
- ListingCard shows a compact verified skill badge only when the penyedia jasa has a badge for that listing category
- ListingDetail penyedia jasa card shows identity plus the relevant skill badge and observes the penyedia jasa's public user badge fields as the display source

---

## Features NOT in MVP

| Feature | Reason |
|---|---|
| Real payment gateway (Midtrans) | Needs verified business account + significant integration time |
| Push notifications | FCM setup complexity |
| Admin panel | Use Firebase Console directly for verification review |
| Automatic OCR / automatic verification | Manual review is enough for MVP |
| KTP or non-KTM identity documents | Privacy and scope control; KTM only |
| Order revision flow | Deferred in PRD to avoid changing the working order lifecycle |
| Dispute / complaint system | Handle manually during MVP |
| Multiple listing photos | One thumbnail is enough for concept validation |
| Multiple portfolio files per item | One optional image per portfolio item is enough for MVP |
| Cloud Functions badge automation | Admin manually syncs submission status and public user badge fields |
| iOS support | Android first |
| Fund withdrawal | No real money in MVP |
