# Phase 5 Maestro flows (scaffolded)

These flows depend on seeded Firestore data:
- 10 listings across 3 sellers
- 3 orders in various states (pending, in_progress, paid)
- A second test account that does NOT own any listing the buyer can view

They will fail today because the test account has nothing to look at.

## Activation checklist

When Phase 5 seed data is in place:

1. Add to `.maestro/.env`:
   - `BERIMA_TEST_LISTING_TITLE` - exact title of a seeded listing the test
     buyer does NOT own
2. (Optional) Replace the seller account in `.env` if cross-account flows
   are needed later.
3. Move flows from `flows-phase5/` into `flows/` so they run in the default
   suite, and add their names to `executionOrder.flowsOrder` in `config.yaml`.
4. Adjust the `# TODO` markers inside each flow - they note any spots that
   need a real value or a wider assertion.

## Files

| File | Covers |
|---|---|
| `60-listing-detail.yaml` | Listing detail view as a non-owner |
| `61-create-order.yaml` | CreateOrder form + Konfirmasi Pesanan happy path |
| `62-user-profile.yaml` | Tap a seller's name on listing detail -> UserProfile |
| `63-create-review.yaml` | Star selector + comment + submit (needs a paid order) |
