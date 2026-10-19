# 003 — Theme, shared pieces and the Home layout

Decided in task 0.3 (19 Oct 2026).

## Colours
Decision: The app has its own colours, in `core/ui/.../theme/Color.kt`. The main colour is a trustworthy blue,
marigold orange is the accent (the Tools icons), and the background is a warm off-white, like paper.
The app never uses the phone's wallpaper colours ("Material You" / dynamic colour).
Why: the app looks the same on every phone and matches the Play Store screenshots, and we know every
colour pair is easy to read. Light or dark follows the phone's setting until Settings (task 2.7) adds a switch.

How the colours were made: five base colours, each given as a hue (colour angle) and chroma (strength) in the
CIELAB colour space:
blue 285° / 70, grey-blue 285° / 12, marigold 62° / 60, red 30° / 75, warm grey 75° / 3 (and 75° / 7 for outlines).
Each screen colour is one shade ("tone", 0 = black, 100 = white) of a base colour, using Material 3's rules:
for example, the main colour is tone 40 in light mode and tone 80 in dark mode, and text on it is tone 100 / 20.
Because tone follows lightness, these rules keep text readable: every text/background pair is at least 4.5 : 1
(the WCAG AA level), and most are above 7 : 1 (AAA). Examples, light / dark: white on blue 6.5 / 7.7,
body text on the background 16.4 / 14.4, grey hint text on cards 8.1 / 9.6.
To change a colour, change its base colour and remake all of its tones, not one value by hand.

Other options we looked at: Material's default purple (looks like every other app); dynamic colour
(the app would look different on every phone, and some wallpapers give poor contrast).

## Text
Decision: The app uses the phone's own font. Most text styles are about 2 sp bigger than Material's defaults,
lines are further apart, and letter spacing is 0 (`core/ui/.../theme/Type.kt`).
Why:
- No font file to download, so the app stays small. Hindi shows in the phone's built-in Hindi font, which renders correctly.
- Many users are older or have weak eyesight.
- Hindi letters have marks above and below the line (ि, ी, ु). With Material's tight line spacing they can
  touch the line above or get cut off. Extra letter spacing breaks the line along the top of Hindi words.
- Everything is in sp, so it grows with the phone's font size. Components never use a fixed height
  (only a minimum height), and text wraps instead of being cut off, so screens still work at 200% font size.

## Icons
Decision: Icons are Google's Material Symbols (Rounded, filled), saved as vector drawables in
`core/ui/src/main/res/drawable/ic_*.xml`. They were downloaded once from Google Fonts (Apache 2.0 license).
These are the same icons Android Studio's "Vector Asset → Clip art" offers.
To add one, download `https://fonts.gstatic.com/s/i/short-term/release/materialsymbolsrounded/<name>/fill1/24px.svg`
(or use Vector Asset), and copy the layout of an existing `ic_*.xml`.
Why: Google's old icon library (`material-icons-extended`) is no longer updated and is very large.
A few small XML files add almost nothing to the app's size.
Icons next to a text label are decoration (`contentDescription = null`), so TalkBack doesn't read things twice.
Icon-only buttons, like Settings, always have a label.

## Shared pieces in `:core:ui`
- `BigActionCard`, `ToolTile`, `ProgressScreen`, `ConfirmDialog` (in `component/`).
- `@PreviewLightDarkHindiBigFont` (in `preview/`): put it on a preview function to see it in light,
  dark, Hindi and 200% font at once. Detekt knows about it (`config/detekt/detekt.yml`).
  The Compose rules require the name to start with `Preview`.
- `ProgressScreen` shows "Page X of Y" from plain numbers (`current`, `total`), so `:core:ui` doesn't need
  `:core:model` yet. Task 1.3 can pass the numbers from `JobProgress`.

## Home layout (PRD §5)
- Three big cards: Photos to PDF (the strongest colour), Scan with camera, My PDFs. Each has a short helper line
  under its name, so first-time users know what each one does.
- "Recent PDFs" shows a "No PDFs yet" message until task 3.1 adds the list.
- The Tools grid has two tiles per row. **ID card** is in the grid, because the layout only has room for three big cards.
  **Lock** and **Remove password** are two tiles, so one tap does one job.
- Until each screen is built, tapping a button shows a short "Coming soon" note (a Snackbar).
  The ViewModel turns `ButtonClick` into `showComingSoon = true`; the screen sends `ComingSoonShown` when the note goes away.
  As each feature is built, its button should open its screen instead.
- `HomeUiState.isLoading` (the placeholder from 000) is replaced by `showComingSoon`.
