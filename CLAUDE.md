# CLAUDE.md — PDF Banao

> Put this file at the **root** of the repo. Claude Code reads it at the start of every session.

## What this project is
**PDF Banao** — a free, offline, no-ads Android app that turns photos into PDFs and does everyday PDF jobs (make smaller, join, split, lock, view).
Users: **Indian smartphone users aged 25–55**, many on cheap phones, many prefer Hindi, many not tech-savvy.

Before working on anything, read:
- `docs/PRD.md` — what to build (features + acceptance criteria)
- `docs/ARCHITECTURE.md` — how to build it (modules, patterns, rules)
- `docs/ROADMAP.md` — the task list; work on **one task at a time**
- `docs/decisions/` — past decisions; don't re-open them without asking

## Key facts
- App name: **PDF Banao** (English) / **PDF बनाओ** (Hindi)
- Package: `io.github.<github-username>.pdfbanao` (if `<github-username>` is still a placeholder anywhere, ask the owner for their GitHub username; use lowercase and replace `-` with `_`). This ID can never change after the first Play upload. · minSdk 26 · targetSdk 36 · Kotlin · Jetpack Compose · Material 3 · Hilt · Room · Coroutines
- PDF writing/editing: **PdfBox-Android**. PDF rendering: Android **PdfRenderer**. Scanning: **ML Kit Document Scanner**.
- License: Apache 2.0. Distribution: Google Play only.

## Commands
```bash
./gradlew assembleDebug                 # build
./gradlew testDebugUnitTest             # unit tests (all modules)
./gradlew connectedDebugAndroidTest     # instrumented tests (needs emulator/device)
./gradlew spotlessApply                 # auto-format
./gradlew spotlessCheck detekt lintDebug  # checks CI runs
./gradlew assembleRelease               # check R8/ProGuard rules still work
```
Run `spotlessApply`, `testDebugUnitTest`, and `lintDebug` before saying a task is done.

## How to work on a task
1. Read the task in `docs/ROADMAP.md` and the matching PRD section.
2. Make a short plan first (files to add/change, tests to write). Wait for approval if the task is big.
3. Implement in small steps. Build often.
4. Write/update tests for the change.
5. Run the checks above. Fix all failures.
6. Tick the task box in `ROADMAP.md`. If you made an important choice, add `docs/decisions/NNN-*.md`.
7. Suggest a commit message: `feat(create): reorder pages by drag` (Conventional Commits).
8. Explain what you did **in simple language** — the owner prefers plain explanations over jargon.

## Rules — always
- Follow the module rules: features → core only; features never import other features; no PDF/file logic in ViewModels or Composables.
- Every screen: `UiState` data class + `Event` sealed interface + `@HiltViewModel` + stateless `Screen` composable + `Route` composable.
- **All user-visible text goes in `core/ui/src/main/res/values/strings.xml` AND `values-hi/strings.xml`.** Never hard-code text in Kotlin. Mark new Hindi strings with a `<!-- TODO review -->` comment so a native speaker can check them.
- Use the simple words from PRD §7 ("Make PDF smaller", not "Compress").
- Heavy work runs through `JobRunner`, off the main thread, one page at a time. Always downsample images. Always `recycle()` bitmaps after writing a page.
- Write to a temp file, then move to the final place. Clean temp files on failure/cancel.
- Never overwrite the user's original file unless they explicitly chose to.
- Touch targets ≥ 48 dp. Every icon button has a `contentDescription`. Layouts must work at 200% font size.
- Edge-to-edge with proper insets; use `BackHandler` / predictive back (no `onBackPressed()` overrides).
- Add new dependencies only via `gradle/libs.versions.toml`, latest stable version.

## Rules — never (ask the owner first if you think you need one of these)
- Never add the `INTERNET` permission, analytics, ads, Firebase, or any tracking SDK.
- Never request `MANAGE_EXTERNAL_STORAGE`, `READ_EXTERNAL_STORAGE` (beyond the API ≤ 28 write case), or `READ_MEDIA_*`.
- Never use iText or any AGPL/GPL library.
- Never log passwords or file contents.
- Never commit keystores, passwords, or `local.properties`.
- Never do large refactors outside the current task.

## Sample files for tests
Put small test images/PDFs in `core/pdf/src/androidTest/assets/` (keep each under 200 KB). Include: portrait photo, landscape photo, photo with EXIF rotation, 3-page PDF, password-locked PDF (password `test123`).
