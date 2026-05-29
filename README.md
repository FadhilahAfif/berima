# Berima

Mobile marketplace for student micro-gigs. Android, Jetpack Compose, Firebase.

> Status: pre-demo. Phases 1–4 complete, Phase 5 (polish & demo readiness) in progress.
> See `AGENTS.md` → Milestones for the live status.

## Tech stack

| Layer        | Tool                                          |
| ------------ | --------------------------------------------- |
| UI           | Jetpack Compose, Material 3                   |
| DI           | Hilt                                          |
| Navigation   | Navigation Compose                            |
| Backend      | Firebase Auth + Firestore + Storage           |
| Image load   | Coil                                          |
| Async        | Kotlin Coroutines                             |
| E2E tests    | Maestro                                       |
| Min / target | minSdk 26, targetSdk 36                       |
| JVM          | Java 17 (build), Java 11 (compile target)     |

## Repo layout

```
app/                       Android app module
.agents/context/           Source-of-truth specs (read these before coding)
.github/                   CI, templates, Dependabot, CODEOWNERS
.maestro/                  E2E flow definitions
scripts/                   Firestore seed + admin helpers
firestore.rules            Deployed via `firebase deploy --only firestore:rules`
firestore.indexes.json     Deployed via `firebase deploy --only firestore:indexes`
AGENTS.md                  How to work on this repo (read first)
DESIGN.MD                  Design system tokens & UI components
```

## Local setup

1. **Clone**
   ```sh
   git clone https://github.com/FadhilahAfif/berima.git
   cd berima
   ```

2. **Drop in Firebase config**
   Place `app/google-services.json` from the Firebase console (project owner can share).
   This file is gitignored and must never be committed.

3. **Build**
   ```sh
   ./gradlew assembleDebug
   ```

4. **Install on a device / emulator**
   ```sh
   ./gradlew installDebug
   ```

5. **Run E2E flows** (requires [Maestro](https://maestro.mobile.dev/))
   ```sh
   maestro test .maestro/flows/00-smoke.yaml
   ```

## Working in this repo

Read these in order before writing code:

1. `AGENTS.md` — how tasks are scoped, milestone tracking
2. `.agents/context/project.md` — what the app is, who it's for
3. `.agents/context/features.md` — full feature spec
4. `.agents/context/database.md` — Firestore schema + rules
5. `.agents/context/architecture.md` — folder structure, deps
6. `.agents/context/conventions.md` — naming, patterns, language rules
7. `DESIGN.MD` — visual design system

Hard rules:
- All user-facing text in **Bahasa Indonesia**
- All code, comments, and identifiers in **English**
- Never commit `app/google-services.json`, `local.properties`, or any keystore

## Contributing

- Branch off `main`. Use `feat/...`, `fix/...`, `chore/...`, `docs/...`.
- Open a PR — the template covers the checklist.
- One human approval required before merge. Squash-merge only.
- Update the milestone table in `AGENTS.md` when status changes.
- Capture any new pattern or gotcha in `.agents/context/learned.md`.

## License

Not yet licensed. Treat the code as "all rights reserved" until a `LICENSE` file is added.
