# Phase 5 Maestro flows (archived stubs)

These flows were scaffolded before Phase 5 seed data was in place. Phase 5 is
now complete — seeded data, security rules, and indexes are all deployed.

The active versions of these flows live in `flows/` and are governed by
`executionOrder.flowsOrder` in `config.yaml`. Do not run this directory directly.

## Remaining manual adjustments

Some flows still have `# TODO` markers for spots that need a real value:

- `BERIMA_TEST_LISTING_TITLE` — exact title of a seeded listing the buyer does NOT own (set in `.maestro/.env`)
- `BERIMA_TEST_PAID_ORDER_TITLE` — title of a paid order with `hasReview=false` (set in `.maestro/.env`)
- Star/submit locators in `63-create-review.yaml` — verify against `CreateReviewScreen.kt`

Credentials are stored in `.maestro/.env` (gitignored). Never commit passwords.

## Files

| File | Covers |
|---|---|
| `60-listing-detail.yaml` | Listing detail view as a non-owner |
| `61-create-order.yaml` | CreateOrder form + Konfirmasi Pesanan happy path |
| `62-user-profile.yaml` | Tap a seller's name on listing detail -> UserProfile |
| `63-create-review.yaml` | Star selector + comment + submit (needs a paid order) |

