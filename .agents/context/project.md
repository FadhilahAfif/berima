# Project Context

## What is Berima

Berima ("Beri Jasa, Terima Hasil") is an Android marketplace app where university students
buy and sell small digital services (micro-gigs) that can be completed in under 48 hours.

Tagline: "Tugas Tuntas, Kantong Pas."

## The Problem

Student-to-student service transactions currently happen informally through WhatsApp groups.
This causes three problems:
- **Unsafe** — ghosting after payment, no accountability
- **Unstructured** — no ratings, no portfolio, no transaction history
- **No existing fit** — Fiverr/Fastwork are too complex and expensive for micro-scale transactions

## The Solution

A campus-specific C2C marketplace with:
- Verified users (university email only)
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

Only students with `@upnvj.ac.id` email can register. Enforced via client-side regex validation.

## Project Status

This is an MVP built for a Technopreneurship course demo and a campus bazaar.
Goal: a working, demonstrable app — not a production system.
Payment is simulated (no real money). Some features are intentionally simplified.
