# Project Context

## What is Berima

Berima ("Beri Jasa, Terima Hasil") is an Android marketplace app where students
buy and sell small digital services (micro-gigs) that can be completed in under 48 hours.

Tagline: "Tugas Tuntas, Kantong Pas."

## The Problem

Student-to-student service transactions currently happen informally through WhatsApp groups.
This causes three problems:
- **Unsafe** — ghosting after payment, no accountability
- **Unstructured** — no ratings, no portfolio, no transaction history
- **No existing fit** — Fiverr/Fastwork are too complex and expensive for micro-scale transactions

## The Solution

A student-focused C2C marketplace with:
- Any valid email registration, plus Google login in the next PRD scope
- Optional Verification Center for identity and skill trust badges
- Public seller portfolios to show work evidence
- Structured listings with categories and fixed pricing
- Order tracking with clear status flow
- Rating and review system to build trust
- In-order chat between buyer and seller

## Service Categories

| ID | Name | Examples |
|---|---|---|
| `academic` | Academic Support | Thesis formatting, bibliography, abstract proofreading |
| `visual` | Visual Branding | PPT design, CV design, UKM poster |
| `data` | Data Processing | SPSS/Excel data processing, document retyping |

## Target Users

**Seller** — student with a digital skill, wants to earn money, not confident enough for Upwork

**Buyer** — student who needs quick help with a small task, budget Rp10.000–Rp100.000

One user can be both seller and buyer simultaneously.

## User Restriction (MVP)

Any valid email address can register. Enforced via client-side regex validation using `android.util.Patterns.EMAIL_ADDRESS`.
Google login is part of the next PRD scope as a simple login/register option; account linking is not required for MVP.

Verification is optional. Users can browse, order, create listings, and chat without verification.
Verified identity/skill badges are trust signals, not access gates.

## Project Status

This is an MVP built for a Technopreneurship course demo.
Goal: a working, demonstrable app — not a production system.
Payment is simulated (no real money). Some features are intentionally simplified.

`.agents/PRD.md` defines the next implementable scope:
- Verification Center from Profile
- Identity Verification using KTM only
- Skill Verification for `academic`, `visual`, and `data`
- Public portfolio items
- Manual admin review through Firebase Console
- Private Firebase Storage paths for identity and skill evidence
- Forgot password, service policy acknowledgement, listing deactivation, and clearer escrow simulation copy

The current PRD intentionally defers real payment, automatic escrow, order revision, dispute/refund handling,
admin panel, Cloud Functions badge automation, KTP support, and multi-campus verification.
