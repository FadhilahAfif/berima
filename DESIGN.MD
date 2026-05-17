---
name: Campus Harmony
colors:
  surface: '#FFFFFF'
  surface-dim: '#dcd9d9'
  surface-bright: '#fcf9f8'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f6f3f2'
  surface-container: '#f0eded'
  surface-container-high: '#eae7e7'
  surface-container-highest: '#e5e2e1'
  on-surface: '#1c1b1b'
  on-surface-variant: '#404943'
  inverse-surface: '#313030'
  inverse-on-surface: '#f3f0ef'
  outline: '#707973'
  outline-variant: '#bfc9c1'
  surface-tint: '#2c694e'
  primary: '#0f5238'
  on-primary: '#ffffff'
  primary-container: '#2d6a4f'
  on-primary-container: '#a8e7c5'
  inverse-primary: '#95d4b3'
  secondary: '#5f5e5a'
  on-secondary: '#ffffff'
  secondary-container: '#e5e2dc'
  on-secondary-container: '#656460'
  tertiary: '#005236'
  on-tertiary: '#ffffff'
  tertiary-container: '#006d48'
  on-tertiary-container: '#89edba'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#b1f0ce'
  primary-fixed-dim: '#95d4b3'
  on-primary-fixed: '#002114'
  on-primary-fixed-variant: '#0e5138'
  secondary-fixed: '#e5e2dc'
  secondary-fixed-dim: '#c9c6c1'
  on-secondary-fixed: '#1c1c18'
  on-secondary-fixed-variant: '#474743'
  tertiary-fixed: '#92f7c3'
  tertiary-fixed-dim: '#75daa8'
  on-tertiary-fixed: '#002113'
  on-tertiary-fixed-variant: '#005235'
  background: '#fcf9f8'
  on-background: '#1c1b1b'
  surface-variant: '#e5e2e1'
  surface-raised: '#F7F5F0'
  border-subtle: '#E8E5E0'
  container-green: '#D4EDE3'
  text-secondary: '#6B6864'
  star-rating: '#FBBF24'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '800'
    lineHeight: 36px
  display-price:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '800'
    lineHeight: 32px
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 22px
    fontWeight: '700'
    lineHeight: 30px
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '700'
    lineHeight: 26px
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 22px
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '800'
    lineHeight: 20px
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 10px
    fontWeight: '600'
    lineHeight: 14px
    letterSpacing: 0.1em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  grid-unit: 4px
  margin-mobile: 16px
  margin-desktop: 24px
  gutter: 12px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 24px
---

## Brand & Style

The design system is crafted for a student-centric marketplace, balancing the energy of campus life with the reliability of a professional exchange. The brand personality is **approachable, organic, and grounded**, evoking a sense of community and trust. 

The visual style follows a **Layered Flat** philosophy. It rejects the artificiality of shadows in favor of **Tonal Layering**, where depth is communicated through subtle shifts in background warmth and high-contrast hairlines. The interface feels "tactile" not through skeuomorphism, but through generous roundedness and a soft, "cream-and-forest" palette that feels easy on the eyes during late-night study sessions. It is a clean, modern aesthetic that prioritizes clarity, especially regarding financial transactions and micro-gig listings.

## Colors

The palette is strictly "Green Light Mode," utilizing a warm cream (`#F2EFE9`) as the foundation to avoid the sterile feel of pure white. **Forest Green** serves as the primary brand anchor for action-oriented elements and active states.

- **Primary:** Forest Green, used for high-impact CTAs and brand identity.
- **Secondary (Base):** Warm Cream, the canvas for the entire application.
- **Surface:** Pure White, reserved specifically for cards and interactive containers to make them "pop" against the cream base.
- **Typography:** Deep Charcoal (`#1A1A1A`) is used for headlines and prices to ensure maximum legibility and professional weight.
- **Constraints:** The system strictly prohibits the use of orange, blue, or purple (except for specific status indicators) to maintain brand purity.

## Typography

This design system uses **Plus Jakarta Sans** exclusively to maintain a contemporary and friendly tone. 

The most critical typographic rule concerns **Price values**: they must always use the **ExtraBold (800)** weight and the `#1A1A1A` charcoal color. This ensures that in a marketplace environment, the "bottom line" is always the most prominent element. For mobile layouts, `display-lg` should scale down to `headline-lg` to prevent text clipping. `label-sm` is intended for category tags and should be implemented in all-caps.

## Layout & Spacing

The system is built on a **4dp baseline grid**. A **Fluid Grid** model is used for mobile and tablet devices, transitioning to a **Fixed Max-Width Grid (1200px)** for desktop to maintain readability.

- **Margins:** Standard screen padding is 16px on mobile, providing a comfortable safe area.
- **Gutters:** 12px for list items and 8px for grid-based cards.
- **Rhythm:** Vertical spacing follows a modular scale (8, 16, 24, 48) to create clear separation between gig categories and seller sections.

## Elevation & Depth

This design system deliberately **eschews drop shadows**. Depth is created through a "Stacking" logic:

1.  **Level 0 (Floor):** The Warm Cream (`#F2EFE9`) background.
2.  **Level 1 (Card):** White (`#FFFFFF`) surfaces. To define these surfaces without shadows, a **1dp hairline border** (`#E8E5E0`) is mandatory.
3.  **Level 2 (Float):** Elevated surfaces like Bottom Sheets or App Bars use `#F7F5F0` with a more pronounced border or a backdrop scrim (`50% Black`) for modals.

Interactive states (hover/pressed) are indicated by a subtle darkening of the surface (`#EEEBE4`) rather than an increase in shadow.

## Shapes

The shape language is **highly rounded**, reinforcing the "campus-friendly" and "soft" aesthetic.

- **Buttons:** Must always be **Full Pill (24dp+)** to create a distinct, friendly call-to-action.
- **Cards/Inputs:** Use a standard **12px (radius_md)** corner radius to feel substantial yet approachable.
- **Tags/Chips:** Use a tighter **6px (radius_sm)** for information density.
- **Avatars:** Strictly circular (50% radius) to differentiate people from service listings.

## Components

### Buttons
Primary buttons are **Pill-shaped (24dp)**, using the Forest Green (`#2D6A4F`) background with white text. Hover states shift to `BrandPrimaryDim` (`#3D8B68`). Label text must use `label-lg` (ExtraBold).

### Cards
Cards are the heart of the marketplace. They utilize a white background, a 12px corner radius, and a 1dp `#E8E5E0` border. No shadows. Content within cards should follow the 16px internal padding rule.

### Input Fields
Inputs use a 1.5dp border (`#D0CCC8`) in their default state, which thickens to 2dp Forest Green on focus. Corners are 12px.

### Chips & Tags
Status chips for "Pending" or "Completed" use a light background and a darker text variant. For example, "Completed" uses a light green container with dark green text. Filter chips follow the pill-shaped button geometry but with `BrandContainer` colors.

### Lists
List items are separated by a 1dp `#E8E5E0` divider. Active or pressed list items use the `#EEEBE4` overlay to indicate selection.

### Micro-Gig Grid
Listings should display the price prominently in the bottom right or top right of the card info area using the `display-price` or `headline-medium` (ExtraBold) style.