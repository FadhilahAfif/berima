# Berima Maestro Test Suite

End-to-end UI flows for the Berima Android app. Used by the agent (and humans) to walk
every screen, capture screenshots, and surface UI/UX regressions.

## Layout

```
.maestro/
├── config.yaml           Workspace config + flow ordering
├── .env.example          Template - copy to .env and fill in
├── run.ps1               PowerShell runner (loads .env, captures output)
├── flows/                Active suite (Phase 1-4 - runnable today)
│   ├── 00-smoke.yaml
│   ├── 01-auth-login.yaml
│   ├── 02-auth-register-validation.yaml
│   ├── 10-home.yaml
│   ├── 11-search.yaml
│   ├── 20-profile-own.yaml
│   ├── 21-edit-profile.yaml
│   ├── 30-listing-create.yaml
│   ├── 31-listing-edit.yaml
│   ├── 40-orders-empty.yaml
│   └── 50-navigation.yaml
├── flows-phase5/         Stubs - need seeded Firestore data
│   ├── README.md
│   ├── 60-listing-detail.yaml
│   ├── 61-create-order.yaml
│   ├── 62-user-profile.yaml
│   └── 63-create-review.yaml
└── subflows/             Reusable steps included via `runFlow`
    ├── login.yaml
    └── logout.yaml
```

## Setup

1. Install Maestro: https://docs.maestro.dev/getting-started/installing-maestro
2. Copy env template:
   ```powershell
   Copy-Item .maestro\.env.example .maestro\.env
   ```
3. Fill in `.maestro/.env`:
   - Create a real Firebase Auth account (email/password) in your Berima project
   - Set `BERIMA_TEST_EMAIL` / `BERIMA_TEST_PASSWORD`
4. Build and install the app on a connected device or emulator:
   ```powershell
   .\gradlew installDebug
   ```

## Running

```powershell
# Full Phase 4 suite (flows/*.yaml in order)
.\.maestro\run.ps1

# Single flow by short name
.\.maestro\run.ps1 -Flow 00-smoke
.\.maestro\run.ps1 -Flow 30-listing-create

# Phase 5 flow (needs seeded data)
.\.maestro\run.ps1 -Flow flows-phase5/60-listing-detail

# Direct CLI (no .env loading)
maestro test .maestro/flows/00-smoke.yaml
```

Output (HTML report + screenshots) lands in `.maestro/output/<timestamp>/`.

## Phase coverage

| Phase | Status | Flows |
|---|---|---|
| 1 - Foundation | covered | 00-smoke, 01-auth-login, 02-auth-register-validation |
| 2 - Core Listing | partial | 10-home, 11-search (detail in Phase 5) |
| 3 - Order Flow | empty states only | 40-orders-empty (detail in Phase 5) |
| 4 - Profile & Review | covered | 20-profile-own, 21-edit-profile, 30-listing-create, 31-listing-edit |
| 5 - Polish | scaffolded | flows-phase5/* (activate after seed data lands) |

## Why split Phase 4 / Phase 5

Phase 5 (`.agents/context/AGENTS.md`) seeds 10 listings, 3 sellers, 3 orders. Until
that lands the test account has no other-user listings to view, no orders to inspect,
and no completed orders to review. Those flows are scaffolded with `# TODO` markers;
move them into `flows/` once data is seeded.

## Conventions

- App ID is `upnvj.berima.v1` - hardcoded as `appId` in every flow
- All UI text is Bahasa Indonesia - assertions match the runtime strings exactly
- Every screen entry takes a screenshot named `<flow>-<state>` for easy visual diff
- Login is reused via `runFlow: ../subflows/login.yaml`
- Created listings use `BERIMA_RUN_ID` to avoid duplicate-title collisions
- Flows tolerate missing data (empty states are first-class assertions)
