---
name: Campus Harmony
colors:
  background: '#F2EFE9'
  surface: '#FFFFFF'
  surface-dim: '#dcd9d9'
  surface-bright: '#fcf9f8'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f6f3f2'
  surface-container: '#f0eded'
  surface-container-high: '#eae7e7'
  surface-container-highest: '#e5e2e1'
  surface-raised: '#F7F5F0'
  surface-pressed: '#EEEBE4'
  on-surface: '#1A1A1A'
  on-surface-variant: '#404943'
  on-background: '#1A1A1A'
  inverse-surface: '#313030'
  inverse-on-surface: '#f3f0ef'
  outline: '#707973'
  outline-variant: '#bfc9c1'
  border-subtle: '#E8E5E0'
  border-input: '#D0CCC8'
  surface-tint: '#2D6A4F'
  primary: '#2D6A4F'
  primary-dim: '#3D8B68'
  on-primary: '#ffffff'
  primary-container: '#0f5238'
  on-primary-container: '#a8e7c5'
  inverse-primary: '#95d4b3'
  primary-fixed: '#b1f0ce'
  primary-fixed-dim: '#95d4b3'
  on-primary-fixed: '#002114'
  on-primary-fixed-variant: '#0e5138'
  secondary: '#5f5e5a'
  on-secondary: '#ffffff'
  secondary-container: '#e5e2dc'
  on-secondary-container: '#656460'
  secondary-fixed: '#e5e2dc'
  secondary-fixed-dim: '#c9c6c1'
  on-secondary-fixed: '#1c1c18'
  on-secondary-fixed-variant: '#474743'
  tertiary: '#005236'
  on-tertiary: '#ffffff'
  tertiary-container: '#006d48'
  on-tertiary-container: '#89edba'
  tertiary-fixed: '#92f7c3'
  tertiary-fixed-dim: '#75daa8'
  on-tertiary-fixed: '#002113'
  on-tertiary-fixed-variant: '#005235'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  container-green: '#D4EDE3'
  text-secondary: '#6B6864'
  star-rating: '#FBBF24'
status:
  pending:
    container: '#FFF4D6'
    text: '#7A5800'
  in-progress:
    container: '#E5EFE9'
    text: '#0f5238'
  delivered:
    container: '#D4EDE3'
    text: '#005236'
  completed:
    container: '#b1f0ce'
    text: '#002114'
  paid:
    container: '#2D6A4F'
    text: '#FFFFFF'
  cancelled:
    container: '#E5E2E1'
    text: '#404943'
  rejected:
    container: '#ffdad6'
    text: '#93000a'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 28sp
    fontWeight: '800'
    lineHeight: 36sp
  display-price:
    fontFamily: Plus Jakarta Sans
    fontSize: 24sp
    fontWeight: '800'
    lineHeight: 32sp
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 22sp
    fontWeight: '700'
    lineHeight: 30sp
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 18sp
    fontWeight: '700'
    lineHeight: 26sp
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 16sp
    fontWeight: '600'
    lineHeight: 24sp
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16sp
    fontWeight: '400'
    lineHeight: 24sp
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14sp
    fontWeight: '400'
    lineHeight: 22sp
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14sp
    fontWeight: '800'
    lineHeight: 20sp
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 10sp
    fontWeight: '600'
    lineHeight: 14sp
    letterSpacing: 0.1em
fonts:
  family: Plus Jakarta Sans
  source: bundled
  location: app/src/main/res/font/
  files:
    - plus_jakarta_sans_regular.ttf
    - plus_jakarta_sans_semibold.ttf
    - plus_jakarta_sans_bold.ttf
    - plus_jakarta_sans_extrabold.ttf
  weights: '400 / 600 / 700 / 800'
  download: https://fonts.google.com/specimen/Plus+Jakarta+Sans
rounded:
  sm: 6px
  md: 12px
  lg: 16px
  xl: 24px
  full: 9999px
spacing:
  grid-unit: 4dp
  margin: 16dp
  gutter-list: 12dp
  gutter-grid: 8dp
  stack-sm: 8dp
  stack-md: 16dp
  stack-lg: 24dp
  stack-xl: 48dp
---

## Brand & Style

Berima is a student-to-student marketplace for micro-gigs. The visual identity is **approachable, organic, and grounded** — a cream-and-forest palette that feels easy on the eyes during late-night study sessions.

Depth is communicated through **tonal layering**: warm cream as the floor, white cards that pop against it, hairline borders instead of shadows. Shapes are generously rounded to reinforce the campus-friendly tone. Every financial value is given maximum visual weight so the "bottom line" is always the most prominent element on screen.

## Colors

The palette is strictly **Green Light Mode**. Warm Cream (`background`) is the canvas for the entire application. Forest Green (`primary`) anchors all action-oriented elements and active states. White (`surface`) is reserved for cards and interactive containers so they lift off the cream floor.

- **Primary:** `primary` (`#2D6A4F`) — high-impact CTAs, active states, brand identity.
- **Primary pressed:** `primary-dim` (`#3D8B68`) — pressed state on primary buttons.
- **Canvas:** `background` (`#F2EFE9`) — screen background, never used on cards.
- **Card surface:** `surface` (`#FFFFFF`) — cards and input containers.
- **Elevated surface:** `surface-raised` (`#F7F5F0`) — bottom sheets, top app bars.
- **Pressed overlay:** `surface-pressed` (`#EEEBE4`) — interactive list items and card press states.
- **Headlines / prices:** `on-surface` (`#1A1A1A`) — maximum legibility, professional weight.
- **Secondary text:** `text-secondary` (`#6B6864`) — supporting copy, metadata.
- **Card border:** `border-subtle` (`#E8E5E0`) — mandatory 1dp hairline on all cards.
- **Input border:** `border-input` (`#D0CCC8`) — default input field border.
- **Star rating:** `star-rating` (`#FBBF24`) — the only warm hue in the system outside of the `pending` status chip.

**Color constraints:** Orange, blue, and purple are prohibited. Yellow appears only as `star-rating` and as the `pending` status chip container — nowhere else.

## Typography

**Plus Jakarta Sans** is used exclusively. It must be bundled as static `.ttf` files in `app/src/main/res/font/` (see `fonts` block in front-matter). Do not use the Downloadable Fonts API — bundling ensures offline availability.

**Critical rule — prices:** All price values must use `display-price` or `display-lg` style (ExtraBold 800) with `on-surface` (`#1A1A1A`) color. In a marketplace context, the price is always the most prominent element.

**Critical rule — labels:** `label-sm` is for category tags and status chips. Always render in ALL CAPS with `letterSpacing: 0.1em`.

## Layout & Spacing

Built on a **4dp baseline grid**. Single-column mobile layout throughout.

- **Screen padding:** 16dp on all sides (`margin`).
- **List gutters:** 12dp between list items (`gutter-list`).
- **Grid gutters:** 8dp between grid cards (`gutter-grid`).
- **Vertical rhythm:** 8 / 16 / 24 / 48dp (`stack-sm` through `stack-xl`). Use `stack-xl` (48dp) for separation between major sections like category headers and listing grids.

## Elevation & Depth

Drop shadows are not used. Depth is created through surface stacking:

1. **Level 0 — Floor:** `background` (`#F2EFE9`). The screen background.
2. **Level 1 — Card:** `surface` (`#FFFFFF`). All cards and input containers. A mandatory 1dp `border-subtle` hairline defines the edge without a shadow.
3. **Level 2 — Float:** `surface-raised` (`#F7F5F0`). Bottom sheets and top app bars. Use a backdrop scrim (`Black 50%`) for modal overlays.

Pressed states are indicated by a surface shift to `surface-pressed` (`#EEEBE4`), not by shadow increase.

## Shapes

- **Buttons:** `rounded.full` (9999px) — pill shape, no exceptions.
- **Cards / Inputs:** `rounded.md` (12px) — substantial yet approachable.
- **Chips / Tags:** `rounded.sm` (6px) — tight radius for information density.
- **Bottom sheets:** `rounded.lg` (16px) on top corners only.
- **Avatars:** Circular (`rounded.full`) — differentiates people from service listings.

## Components

### Buttons

Primary buttons are pill-shaped (`rounded.full`), `primary` background, `on-primary` (white) text, `label-lg` (ExtraBold 14sp). Pressed state shifts background to `primary-dim`. Disabled state uses `surface-container` background with `on-surface-variant` text.

### Cards

White (`surface`) background, `rounded.md` (12px) corners, 1dp `border-subtle` hairline border. No shadows. Internal padding 16dp on all sides. Pressed state shifts background to `surface-pressed`.

### Input Fields

`surface` background, `rounded.md` (12px) corners. Default border: 1.5dp `border-input`. Focused border: 2dp `primary`. Error border: 2dp `error`. Label uses `body-md`, hint text uses `text-secondary`.

### Chips & Tags

Pill-shaped (`rounded.full`), `label-sm` ALL CAPS, vertical padding 4dp, horizontal padding 10dp. Colors come from the `status` token map (see Status Chip section below) or from `secondary-container` / `on-secondary-container` for filter chips.

### Lists

List items separated by a 1dp `border-subtle` divider. Pressed state uses `surface-pressed` overlay.

### Micro-Gig Grid

Listings display price prominently using `display-price` (ExtraBold 24sp) in `on-surface` (`#1A1A1A`). Price is positioned in the card info area, visually dominant over title and seller name.

### Bottom Navigation

Height 80dp. Surface: `surface`. Top hairline: 1dp `border-subtle`. No shadow. Selected icon and label: `primary`. Unselected icon and label: `on-surface-variant`. Active indicator: pill behind selected icon using `primary-fixed` at reduced opacity. Three tabs: **Beranda** (Home) | **Pesanan** (Orders) | **Profil** (Profile).

### Top App Bar

Surface: `surface-raised`. Bottom hairline: 1dp `border-subtle`. No shadow. Title: `headline-md` in `on-surface`. Navigation icon (back arrow): `on-surface`. Action icons: `on-surface-variant`.

### Tab Row

Used in OrdersScreen for "Sebagai Pembeli | Sebagai Penjual". Background: `background`. Active tab: `primary` underline 2dp, label `on-surface`. Inactive tab: label `on-surface-variant`. No indicator fill — underline only.

### Snackbar

Background: `inverse-surface`. Text: `inverse-on-surface`. Shape: `rounded.md`. No action button by default. Every screen exposes one via `SnackbarHost` per conventions.md rule — this is not optional.

### Status Chip

Pill-shaped (`rounded.full`), `label-sm` ALL CAPS, vertical padding 4dp, horizontal padding 10dp. Colors and labels:

| Status | Container | Text | Label |
|---|---|---|---|
| `pending` | `#FFF4D6` | `#7A5800` | MENUNGGU |
| `in_progress` | `#E5EFE9` | `#0f5238` | DIKERJAKAN |
| `delivered` | `#D4EDE3` | `#005236` | TERKIRIM |
| `completed` | `#b1f0ce` | `#002114` | SELESAI |
| `paid` | `#2D6A4F` | `#FFFFFF` | DIBAYAR |
| `cancelled` | `#E5E2E1` | `#404943` | DIBATALKAN |
| `rejected` | `#ffdad6` | `#93000a` | DITOLAK |

`paid` is the only filled (solid background) chip — this makes the terminal-positive state visually unique. All others use a tinted container with dark text.

### Listing Thumbnail Placeholder

`surface-container` background with a centered category icon in `outline`. Identical dimensions to the loaded image so layout does not shift on load. Used by Coil while the image is loading and when `thumbnailUrl` is null.

### Empty State

Centered layout: icon in `outline`, `headline-sm` title in `on-surface`, `body-md` body in `on-surface-variant`, optional pill CTA button. One canonical layout used by every empty list screen — do not invent per-screen variations.

### Loading State

`CircularProgressIndicator` in `primary`, centered, size 32dp. No skeleton screens for MVP.

### Error State

Same layout as empty state. Icon color: `error`. Body text uses `on-error-container` on `error-container` callout surface. Always paired with a "Coba Lagi" (retry) pill button.

### Bottom Sheet / Dialog

Surface: `surface-raised`. Top corners: `rounded.lg` (16px). Scrim: Black 50%. Used for: category dropdown in CreateListing, confirm-cancel-order dialog, and any destructive confirmation. Handle bar: 4dp × 32dp, `outline-variant`, centered at top.

### Rupiah Formatting

Canonical format: **`Rp10.000`** — no space between `Rp` and the number, dot as thousands separator, no decimal places. Example: `Rp25.000`, `Rp100.000`. Apply consistently across all screens.

## Theme Policy

- **Light only.** `BerimaTheme(darkTheme = false)` — system theme is ignored for MVP.
- **Dynamic color disabled.** `dynamicColor = false` — prevents Android 12+ from overriding the brand palette with wallpaper-extracted colors.
- **Dark mode deferred.** Tokens are organized so dark-mode mappings can be added later without renaming. The `primary-fixed`, `secondary-fixed`, `tertiary-fixed` families are kept specifically for this forward-compatibility.

## Compose Mapping Notes

These notes bridge the design tokens to the Kotlin implementation. No code is written here — this is the contract for `Color.kt`, `Type.kt`, and `Theme.kt`.

- **Units:** All `px` values in this file map 1:1 to `dp` in Compose. All font sizes map to `sp`.
- **M3 ColorScheme slots:** Token names in the front-matter map directly to `ColorScheme` fields (e.g. `primary` → `ColorScheme.primary`, `on-surface` → `ColorScheme.onSurface`).
- **Berima-only tokens:** The following tokens have no M3 `ColorScheme` slot and must live on a `BerimaColors` data class held in a `CompositionLocal` (e.g. `LocalBerimaColors`):
  - `primary-dim`
  - `surface-pressed`
  - `surface-raised`
  - `border-subtle`
  - `border-input`
  - `container-green`
  - `star-rating`
  - All `status.*` entries
- **Status chip colors** are accessed as `LocalBerimaColors.current.statusPending`, `.statusInProgress`, etc. — never as raw hex in a composable.
- **Font loading:** Register all four Plus Jakarta Sans weights in `res/font/` and reference them via a `FontFamily` in `Type.kt`. The `fonts` block in the front-matter is the single source of truth for filenames and weights.
- **Convention (flag for conventions.md):** No raw hex values in composables. All colors flow through `MaterialTheme.colorScheme.*` or `LocalBerimaColors.current.*`.
