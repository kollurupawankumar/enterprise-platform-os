# Design System Guide: Typography & Color Tokens (Bootstrap-aligned)

This document defines typography and color tokens to be used across the MDHCP-UI, aligned with Bootstrap conventions for visual consistency.

## Typography

- Font family (sans): use a system-ui stack aligned to Bootstrap defaults for consistency with the browser’s rendering performance and accessibility.
- Tokenized font family: define in CSS as a design token to ensure consistency across components.
- Font scale (recommended):
  - h1: 2.5rem
  - h2: 2.0rem
  - h3: 1.75rem
  - h4: 1.5rem
  - h5: 1.25rem
  - h6: 1.0rem
- Body text: 1rem (16px on a 16px baseline)
- Line height: 1.5 for body text to improve readability
- Font weights: 400 (regular), 500 (medium), 700 (bold)
- Best practice: use Bootstrap typography utilities (h1–h6 classes, lead, display-*, etc.) where possible; override with tokens for custom components.

Example tokens (CSS):

```
/* mdhcp-ui/docs/design-system/design-tokens.css (referenced by UI) */
:root {
  --ds-font-family-sans: system-ui, -apple-system, "Segoe UI", Roboto, "Helvetica Neue", Arial, "Noto Sans", "Liberation Sans", sans-serif;
  --ds-font-family-mono: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", monospace;
  --ds-font-size-base: 1rem; /* 16px */
  --ds-font-size-h1: 2.5rem;
  --ds-font-size-h2: 2rem;
  --ds-font-size-h3: 1.75rem;
  --ds-font-size-h4: 1.5rem;
  --ds-font-size-h5: 1.25rem;
  --ds-font-size-h6: 1rem;
  --ds-line-height-base: 1.5;
  --ds-font-weight-regular: 400;
  --ds-font-weight-medium: 500;
  --ds-font-weight-bold: 700;
}

html, body {
  font-family: var(--ds-font-family-sans);
  font-size: var(--ds-font-size-base);
  line-height: var(--ds-line-height-base);
}
h1 { font-size: var(--ds-font-size-h1); font-weight: var(--ds-font-weight-bold); }
h2 { font-size: var(--ds-font-size-h2); font-weight: var(--ds-font-weight-bold); }
h3 { font-size: var(--ds-font-size-h3); font-weight: var(--ds-font-weight-bold); }
h4 { font-size: var(--ds-font-size-h4); font-weight: var(--ds-font-weight-bold); }
h5 { font-size: var(--ds-font-size-h5); font-weight: var(--ds-font-weight-bold); }
h6 { font-size: var(--ds-font-size-h6); font-weight: var(--ds-font-weight-bold); }
```

## Color Tokens (Bootstrap-aligned)

- Primary palette mirrors Bootstrap: primary, secondary, success, danger, warning, info, light, dark.
- Design tokens map to Bootstrap colors for consistency across components.
- Core tokens:
  - --ds-color-primary: #0d6efd (Bootstrap 5 primary)
  - --ds-color-secondary: #6c757d
  - --ds-color-success: #198754
  - --ds-color-danger: #dc3545
  - --ds-color-warning: #ffc107
  - --ds-color-info: #0dcaf0
  - --ds-color-light: #f8f9fa
  - --ds-color-dark: #212529
  - --ds-color-text: #212529
  - --ds-color-bg: #f7f7f7
  - --ds-color-border: rgba(0,0,0,.12)

- Additional neutrals can be added for borders, backgrounds, and muted text:
  - --ds-color-gray-100: #f8f9fa
  - --ds-color-gray-200: #e9ecef
  - --ds-color-gray-300: #dee2e6
  - --ds-color-gray-500: #6c757d

- Usage guidance:
  - Apply tokens to custom components and utility classes designed for the design system.
  - For Bootstrap-based components, prefer Bootstrap’s utility classes for layout and spacing, and use tokens for color-related customizations.

Example CSS usage:

```
/* mdhcp-ui/docs/design-system/design-tokens.css */
:root {
  --ds-color-primary: #0d6efd;
  --ds-color-bg: #f7f7f7;
  --ds-color-text: #212529;
}

.btn-ds-primary {
  background-color: var(--ds-color-primary);
  border-color: var(--ds-color-primary);
  color: #fff;
}
.text-ds-muted { color: #6c757d; }
```

## Accessibility

- Ensure a minimum contrast ratio of 4.5:1 for normal text and 3:1 for large text when using tokens.
- Prefer semantic HTML and Bootstrap components with proper ARIA attributes where applicable.

This guide is a lightweight starting point for consistent typography and color use across the UI.
