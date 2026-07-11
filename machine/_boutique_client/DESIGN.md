---
name: VolySaina+
colors:
  surface: '#fff8f2'
  surface-dim: '#e0d9d2'
  surface-bright: '#fff8f2'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#faf2eb'
  surface-container: '#f4ede6'
  surface-container-high: '#eee7e0'
  surface-container-highest: '#e8e1db'
  on-surface: '#1e1b17'
  on-surface-variant: '#404940'
  inverse-surface: '#33302c'
  inverse-on-surface: '#f7efe9'
  outline: '#707a6f'
  outline-variant: '#bfc9bd'
  surface-tint: '#1f6c3a'
  primary: '#004c22'
  on-primary: '#ffffff'
  primary-container: '#166534'
  on-primary-container: '#93e0a2'
  inverse-primary: '#8bd79b'
  secondary: '#9a4614'
  on-secondary: '#ffffff'
  secondary-container: '#fd925b'
  on-secondary-container: '#712c00'
  tertiary: '#653319'
  on-tertiary: '#ffffff'
  tertiary-container: '#81492d'
  on-tertiary-container: '#ffc1a4'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#a6f4b5'
  primary-fixed-dim: '#8bd79b'
  on-primary-fixed: '#00210b'
  on-primary-fixed-variant: '#005226'
  secondary-fixed: '#ffdbcb'
  secondary-fixed-dim: '#ffb693'
  on-secondary-fixed: '#341000'
  on-secondary-fixed-variant: '#7a3000'
  tertiary-fixed: '#ffdbcc'
  tertiary-fixed-dim: '#ffb694'
  on-tertiary-fixed: '#351000'
  on-tertiary-fixed-variant: '#6d391e'
  background: '#fff8f2'
  on-background: '#1e1b17'
  surface-variant: '#e8e1db'
typography:
  headline-xl:
    fontFamily: Inter
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.02em
  headline-xl-mobile:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.05em
  label-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  base: 4px
  gutter-desktop: 24px
  margin-desktop: 48px
  gutter-mobile: 16px
  margin-mobile: 20px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 32px
---

## Brand & Style
The design system is engineered for the heavy-duty agricultural sector, prioritizing utility, reliability, and mechanical precision. The brand personality is rugged yet professional, mirroring the durability of the machinery it represents. The visual style blends **Corporate Modern** efficiency with **Tactile** industrial elements. 

The UI should feel grounded and high-strength. We avoid delicate or ethereal effects in favor of solid blocks, clear divisions, and high-legibility interfaces that work as well in a field office as they do on a desktop. The emotional response should be one of absolute confidence: the user is interacting with a platform as dependable as the equipment they are renting or purchasing.

## Colors
The palette is derived from the landscape of industrial farming. 

- **Primary (Forest Green):** Represents growth, stability, and the environmental core of agriculture. Used for main actions and brand presence.
- **Secondary (Earthy Clay):** An accent color used for notifications, highlights, and specialized "Call to Action" buttons that require high visibility against green.
- **Tertiary (Dark Bark):** Used for deep contrast elements and high-level navigation backgrounds.
- **Neutral (Warm Stone):** A range of greys with warm undertones used for backgrounds and borders to avoid the sterile feel of pure "tech" greys.

Surface colors should prioritize a "Stone 50" (#fafaf9) background for the main canvas to reduce glare while maintaining a clean, professional look.

## Typography
This design system utilizes **Inter** exclusively to maintain a functional, industrial aesthetic. The typeface is chosen for its exceptional legibility at small sizes (essential for spec sheets and equipment manuals) and its neutral, systematic tone.

- **Headlines:** Use tighter letter spacing and heavier weights (600-700) to create a sense of structural integrity.
- **Labels:** Small labels and metadata should use a slightly increased letter spacing and uppercase styling to mimic industrial stamping and equipment plates.
- **Body:** Standardized at 16px for optimal readability in varied lighting conditions.

## Layout & Spacing
The layout follows a **Fixed Grid** philosophy on desktop (1280px max-width) to ensure that dense technical information and large imagery remain organized and easy to scan. 

- **Grid:** A 12-column system is used for desktop, collapsing to 4 columns for mobile. 
- **Rhythm:** An 8px linear scale governs all padding and margins. 
- **Adaptation:** On mobile, horizontal padding is tightened, and complex machinery comparison tables should transition to vertical stacks or horizontally scrollable cards.
- **White Space:** While the brand is rugged, we use generous "stack-lg" spacing between equipment categories to prevent the interface from feeling cluttered or overwhelming.

## Elevation & Depth
In alignment with the "Rugged" tone, this design system avoids soft, floating effects. Instead, it uses **Tonal Layers** and **Low-Contrast Outlines** to define hierarchy.

- **Surfaces:** Use subtle shifts in background color (Stone 50 to Stone 100) to differentiate sections.
- **Borders:** Containers are defined by 1px solid borders in Stone 200. This provides a "framed" look reminiscent of technical drawings.
- **Shadows:** Avoid drop shadows for standard cards. Reserved only for "Overlays" (modals or dropdowns), using a sharp, low-spread shadow with 10% opacity black to maintain a flat, grounded feel.

## Shapes
We use a **Soft** shape language (0.25rem radius) to suggest quality and modern engineering without losing the "hard" edge of heavy machinery. 

- **Buttons:** Use the standard 0.25rem (4px) radius. 
- **Cards & Inputs:** Follow the same 4px radius to maintain a consistent, modular appearance. 
- **Circular Elements:** Reserved exclusively for status indicators (e.g., "Available" vs "In-Use" dots).

## Components
- **Buttons:** Primary buttons use the Forest Green background with white text. Secondary buttons use a thick 2px Stone 200 border. Transitions should be instant or very fast (150ms) to reflect mechanical responsiveness.
- **Input Fields:** Use a Stone 50 background and a 1px border. On focus, the border thickens and changes to Forest Green.
- **Cards:** For machinery listings, cards feature a top-aligned high-quality image, a bold "Headline-MD" for the model name, and a "Label-MD" for the price or horsepower specs.
- **Status Chips:** Used to denote "New Arrival," "Rental Available," or "Sold." These use a semi-transparent fill of the Primary or Secondary color with high-contrast text.
- **Technical Specs Table:** A custom component for equipment details. Rows alternate in Stone 50 and White for readability, with a "Label-MD" column for the attribute name (e.g., TRANSMISSION).
- **Imagery:** Photos should be crisp, utilizing natural lighting. Equipment should be shown in action or in a clean field environment, emphasizing power and scale. Avoid studio-only shots.