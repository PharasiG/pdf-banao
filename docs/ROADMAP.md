# PDF Banao — Roadmap

> Put this file at `docs/ROADMAP.md`.
> Each task is sized for **one Claude Code session**. Do them in order. Tick `[x]` when done.

---

## Part A — How to build this with Claude Code (read once)

### One-time setup (you do this)
1. Install **Android Studio** (latest stable) and create an emulator: Pixel, Android 16. Later also create a small one: 2 GB RAM, Android 8.0.
2. Install **Git** and create an empty GitHub repo `pdf-banao`.
3. Install **Claude Code** and open a terminal in the repo folder.
4. Copy these files in: `CLAUDE.md` (root), `docs/PRD.md`, `docs/ARCHITECTURE.md`, `docs/ROADMAP.md`. Commit them.

### The loop for every task
1. Start a fresh Claude Code session (or `/clear`) — a clean start for each task keeps it focused.
2. Switch to **plan mode** (Shift+Tab) and paste:
   ```
   Do task <number> from docs/ROADMAP.md.
   Read CLAUDE.md, the PRD section for this feature, and ARCHITECTURE.md first.
   Show me your plan before writing code.
   ```
3. Read the plan. If something looks wrong or too big, say so. Then approve.
4. Let it build. When it says done, **run the app yourself** on the emulator and try the feature.
5. If something is wrong, describe what you saw ("the 3rd photo is sideways in the PDF"), not how to fix it.
6. When happy: commit (`git add -A && git commit -m "<message it suggested>"`) and push.

### Tips
- **Small steps win.** If a task feels big, ask Claude Code to split it.
- **Don't let it skip tests.** Say: "Run the tests and show me they pass."
- **If it goes in circles** (same bug 3 times), stop, `/clear`, and start the task again with what you learned.
- **Commit before each task**, so you can always go back: `git checkout .` undoes everything since the last commit.
- **Check in a real phone** at the end of each milestone, ideally a cheap one.

---

## Part B — Tasks

### Milestone 0 — Foundation
- [x] **0.1 Create the project.** Multi-module Gradle project as in ARCHITECTURE §3 (`:app`, `:core:*`, `:feature:*`), version catalog, Kotlin, Compose, Material 3, Hilt, Navigation Compose. Package `io.github.zav.pdfbanao`. minSdk 26, targetSdk/compileSdk 36. App shows an empty Home screen with the title "PDF Banao". Edge-to-edge on. Add a `.gitignore` for Android. Confirm `./gradlew assembleDebug` builds and the app runs on the emulator.
- [x] **0.2 Code quality + CI.** Spotless/ktlint, Detekt, Android Lint. GitHub Actions workflow: build, unit tests, checks on every push and pull request. Add `README.md` and `LICENSE` (Apache 2.0).
- [x] **0.3 Theme + base components.** Light/dark theme, typography that scales well, `BigActionCard`, `ToolTile`, `ProgressScreen`, `ConfirmDialog`. `strings.xml` in English + Hindi with app name ("PDF Banao" / "PDF बनाओ"). Home screen layout from PRD §5 with placeholder buttons.
- [x] **0.4 PDF engine spike.** In `:core:pdf`, a quick test: make a 10-photo PDF with PdfBox-Android (JPEG embedded) and with Android `PdfDocument`. Compare file size and time. Record the result in `docs/decisions/001-pdf-engine.md`. (Expected: PdfBox gives much smaller files.)

**Milestone check:** app opens, looks right in light/dark, CI is green.

### Milestone 1 — Core flow, end to end (can be plain-looking)
- [x] **1.1 Pick photos.** "Photos to PDF" opens the Photo Picker (multi-select, up to 100). Selected photos show as a simple list.
- [ ] **1.2 Image processing.** `:core:image` `ImageProcessor`: decode at reduced size, fix EXIF rotation, encode JPEG at given quality. Unit/instrumented tests with sample photos.
- [ ] **1.3 Make the PDF.** `PdfCreator.createFromImages` with default options (A4, small margin, medium quality), one page at a time, progress callback. `JobRunner` with cancel. Progress screen.
- [ ] **1.4 Name and save.** Name dialog (default name, text pre-selected, bad characters removed, `(1)` for duplicates). Save to `Documents/PDF Banao` via MediaStore (and legacy path for Android 8–9). Room `documents` table + `DocumentRepository`.
- [ ] **1.5 Done screen.** Tick, name, pages, size. Share on WhatsApp (if installed), Share, Open, Save as (SAF), Print, Make another.

**Milestone check:** pick 30 photos → PDF → share on WhatsApp works on the emulator and a real phone. 🎉 This is your first usable app.

### Milestone 2 — Make the create flow great
- [ ] **2.1 Arrange pages screen.** Thumbnail grid with numbers, drag to reorder, delete, "+ Add more".
- [ ] **2.2 Rotate, crop, filter.** Per-page rotate, crop (crop library — record choice in `docs/decisions/`), Original / B&W / Grey filter.
- [ ] **2.3 Options sheet.** Page size, margin, quality, target size; estimated file size shown live. Target-size search logic with unit tests (ARCHITECTURE §6).
- [ ] **2.4 Scan with camera.** ML Kit Document Scanner → pages into Arrange screen. Friendly fallback if Play services unavailable. Check whether it works without `INTERNET` permission; record result.
- [ ] **2.5 Share into the app.** Intent filters for `image/*` (single + multiple) → Arrange screen.
- [ ] **2.6 ID card mode.** Front + back on one A4 page at real card size; "Make bigger" option. Test by printing at 100%.
- [ ] **2.7 Settings (basic).** Language (English/Hindi, per-app language), theme, default quality/page size/margin. DataStore.

**Milestone check:** test with 2–3 real people from the target group. Watch them use it without helping. Note where they get stuck.

### Milestone 3 — My PDFs and viewer
- [ ] **3.1 My PDFs list.** Thumbnails, name, size, pages, date. Search and sort. Remove entries whose files are gone. "Recent PDFs" on Home.
- [ ] **3.2 File actions.** Rename, delete (confirm), share, save as, details. Multi-select: share, delete.
- [ ] **3.3 Viewer.** `PdfRenderer` viewer: scroll, pinch zoom, page indicator, jump to page. Password prompt for locked PDFs.
- [ ] **3.4 Open from outside.** "Open with PDF Banao" (`ACTION_VIEW application/pdf`), share-in for PDFs with quick tools menu, "Open a PDF" button in My PDFs (SAF + persistable permission).

### Milestone 4 — Tools
- [ ] **4.1 Tools hub + shared tool pattern.** Tools grid; reusable pick → options → progress → done flow. Decide foreground-service approach for long jobs; record in `docs/decisions/`.
- [ ] **4.2 Join PDFs.** Pick many, reorder, join. Allow adding photos.
- [ ] **4.3 Split / Take out pages.** Select pages; selected → new PDF; each page separate; every N pages.
- [ ] **4.4 Arrange pages of a PDF.** Reorder, rotate, delete, add photo pages; save as new.
- [ ] **4.5 Make PDF smaller.** Levels + target size; normal (re-compress images) and strong (re-render) methods; before/after size.
- [ ] **4.6 Lock / Unlock.** AES-256 lock with confirm + warning; unlock with password.
- [ ] **4.7 PDF to photos.** All/selected pages → JPGs in `Pictures/PDF Banao/<name>/`; share.

**Milestone check:** every tool works on a 50-page PDF on the low-end emulator without crashing.

### Milestone 5 — Polish, language, accessibility
- [ ] **5.1 Hindi complete.** All strings translated; remove `TODO review` after a native speaker checks them. Check no text is cut off in Hindi.
- [ ] **5.2 Accessibility pass.** 200% font size on every screen, TalkBack labels, 48 dp targets, colour contrast.
- [ ] **5.3 Low-end phone pass.** 2 GB RAM / Android 8 emulator: 100 photos, 50-page merge, rotate phone mid-job, switch apps mid-job. Fix all crashes and freezes.
- [ ] **5.4 Error messages.** Every `AppError` has a simple message that says what to do next. Test storage-full and wrong-password cases.
- [ ] **5.5 First-run touches.** App icon, splash, empty states with helpful text ("No PDFs yet. Tap 'Photos to PDF' to make your first one."). Finish Settings: About, open-source licenses, "Where files are saved", Send feedback (email).

### Milestone 6 — Release on Play Store
- [ ] **6.1 Release build.** R8 + resource shrinking, PdfBox ProGuard rules, Baseline Profile, App Bundle. Test the **release** build fully. App download size < 10 MB.
- [ ] **6.2 Privacy policy.** Simple page on GitHub Pages ("We collect nothing. Files stay on your phone."). Link it in Settings.
- [ ] **6.3 Play Console setup.** Developer account, app created with package `io.github.zav.pdfbanao`, Data safety = no data collected/shared, content rating, target audience (18+ / general).
- [ ] **6.4 Store listing.** English + Hindi title, short and full description, 4–6 screenshots showing the 3-tap flow, feature graphic.
- [ ] **6.5 Testing tracks.** Internal test → closed test. New personal developer accounts may have to run a closed test with a minimum number of testers for 14 days before going to production — check the current rule in Play Console and recruit testers early (friends/family in the target age group).
- [ ] **6.6 Production launch.** Staged rollout (20% → 50% → 100%), watch Android vitals for crashes.

### After launch (v1.x backlog — pick based on reviews)
- [ ] More languages (Bengali, Marathi, Telugu, Tamil, Gujarati, Kannada, Malayalam, Punjabi, Odia)
- [ ] Add page numbers
- [ ] Add watermark text
- [ ] Add signature
- [ ] Copy text from scanned PDFs (OCR with ML Kit — Devanagari + Latin)
- [ ] Photo tools: make a photo smaller / resize (for portals asking for a JPG under X KB)
- [ ] Home-screen shortcuts
