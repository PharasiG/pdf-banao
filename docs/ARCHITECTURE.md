# PDF Banao — Architecture

> Put this file at `docs/ARCHITECTURE.md`.
> This file says **how** the app is built. Written in simple language — each choice has a short "why".

---

## 1. Big picture

```
┌─────────────────────────── :app ───────────────────────────┐
│  MainActivity, navigation graph, Hilt setup, intent handling │
└──────────────┬─────────────────────────────────────────────┘
               │ uses
   ┌───────────┼──────────────┬──────────────┬──────────────┐
   ▼           ▼              ▼              ▼              ▼
:feature:   :feature:      :feature:      :feature:      :feature:
 create      library        viewer         tools          settings
   │           │              │              │              │
   └───────────┴──────┬───────┴──────────────┴──────────────┘
                      ▼ (features only talk to core, never to each other)
   ┌──────────┬──────────┬──────────┬──────────┬──────────┐
   │:core:ui  │:core:data│:core:pdf │:core:image│:core:model│
   └──────────┴──────────┴──────────┴──────────┴──────────┘
```

**Simple rule:** screens (features) ask the core for work. Core modules do the work. Features never import other features.

**Why modules?** Each Claude Code session can work on one module without breaking others, builds are faster, and the PDF code can be tested on its own.

---

## 2. Tech choices

| Area | Choice | Why |
|---|---|---|
| Language | Kotlin | Standard for Android |
| UI | Jetpack Compose + Material 3 | Modern, less code, easy big-font support |
| Screens | Single Activity + Navigation Compose (type-safe routes) | Simple navigation, one place to handle incoming intents |
| State | ViewModel + `StateFlow<UiState>` (one-way data flow) | Screen just draws the state; easy to test |
| Background work | Kotlin Coroutines | Keeps UI smooth |
| Dependency injection | Hilt (with KSP) | Standard, works well with ViewModels |
| Local database | Room | Remembers the list of "My PDFs" |
| Settings storage | DataStore (Preferences) | Simple key-value settings |
| Image loading/thumbnails | Coil 3 | Small, Compose-friendly |
| **Making / editing PDFs** | **PdfBox-Android** (Apache 2.0) | Merge, split, encrypt, and puts photos in as JPEG (small files) |
| **Showing PDFs / thumbnails** | Android `PdfRenderer` (built-in) | No extra size, fast |
| Camera scanning | ML Kit Document Scanner (Google Play services) | Edge detection + crop + filters for free; adds only ~300 KB |
| Picking photos | Android Photo Picker | No storage permission needed |
| Picking/saving files | Storage Access Framework (`OpenDocument`, `CreateDocument`) + MediaStore | Play-Store-safe, no "all files" permission |
| Crop UI | A maintained open-source crop library (e.g. CanHub Android-Image-Cropper) — confirm license & activity at setup | Don't write crop UI from scratch |
| Languages | Android string resources + per-app language API (AppCompat) | Hindi without changing phone language |
| Lint/format | Spotless + ktlint, Detekt, Android Lint | Keeps code consistent across sessions |
| CI | GitHub Actions | Build + tests on every push |

**Not allowed:** iText (AGPL license — would force rules we don't want), any analytics/ads SDK, Firebase, `MANAGE_EXTERNAL_STORAGE` permission.

**Versions:** always use the latest **stable** versions, all listed in one file: `gradle/libs.versions.toml`.

### SDK levels
- `minSdk = 26` (Android 8.0)
- `targetSdk = 36` and `compileSdk = 36` (or newer) — Google Play requires new apps and updates to target API 36 since 31 Aug 2026.
- Because we target 36: the app **must** draw edge-to-edge (use proper insets) and use the **predictive back** APIs (`BackHandler` / `OnBackPressedDispatcher`), not the old `onBackPressed()`.

---

## 3. Modules — what goes where

### `:core:model`
Plain Kotlin data classes. No Android UI code.
- `PageSource` (a photo URI + rotation + crop rect + filter)
- `PdfOptions` (pageSize, margin, quality, targetSizeBytes?)
- `PdfDocumentInfo` (id, uri, name, sizeBytes, pageCount, createdAt, isLocked)
- `JobProgress` (current, total, message), `AppError` (sealed class of simple error types)

### `:core:image`
Everything about photos.
- Decode a photo **at reduced size** (never load a full 12 MP photo into memory if not needed).
- Fix rotation using EXIF.
- Apply rotate / crop / filter (B&W, grey).
- Encode to JPEG at a given quality.
- Key class: `ImageProcessor`.

### `:core:pdf`
Everything about PDFs. Hidden behind interfaces so it can be tested and changed later.

```kotlin
interface PdfCreator {
    suspend fun createFromImages(pages: List<PageSource>, options: PdfOptions,
                                 output: OutputStream, onProgress: (JobProgress) -> Unit)
    suspend fun createIdCard(front: PageSource, back: PageSource, fitWidth: Boolean, output: OutputStream)
}
interface PdfTools {
    suspend fun merge(inputs: List<Uri>, output: OutputStream, onProgress: ...)
    suspend fun extractPages(input: Uri, pages: List<Int>, output: OutputStream)
    suspend fun splitEvery(input: Uri, n: Int, outputs: (index: Int) -> OutputStream)
    suspend fun reorganize(input: Uri, newOrder: List<PageEdit>, output: OutputStream)
    suspend fun compress(input: Uri, level: CompressLevel, output: OutputStream): CompressResult
    suspend fun lock(input: Uri, password: String, output: OutputStream)
    suspend fun unlock(input: Uri, password: String, output: OutputStream)
    suspend fun toImages(input: Uri, pages: List<Int>, quality: Quality, sink: (Int, Bitmap) -> Unit)
}
interface PdfRendererService {
    suspend fun pageCount(uri: Uri, password: String? = null): Int
    suspend fun renderPage(uri: Uri, index: Int, widthPx: Int, password: String? = null): Bitmap
}
```
- Implementations: `PdfBoxCreator`, `PdfBoxTools` (PdfBox-Android), `AndroidPdfRendererService` (`PdfRenderer`).
- Call `PDFBoxResourceLoader.init(context)` once at app start.

### `:core:data`
Where files and records live.
- `DocumentRepository` — list / add / rename / delete / search My PDFs (Room + file system together).
- `FileStore` — save to `Documents/PDF Banao` (MediaStore), "Save as" (SAF), temp files, cleanup.
- `SettingsRepository` — DataStore.
- `JobRunner` — runs one heavy job at a time, reports progress (see §6).

### `:core:ui`
- Theme (colors, typography, shapes), light + dark.
- Shared components: `BigActionCard`, `ToolTile`, `PageThumbnailGrid`, `ProgressScreen`, `DoneScreen`, `ConfirmDialog`, `PasswordField`, `FileSizeText`.
- **All strings live here:** `core/ui/src/main/res/values/strings.xml` (English) and `values-hi/strings.xml` (Hindi). One place = easy for translators.

### `:feature:*`
Each feature = screens + ViewModels only. No PDF or file logic inside.
- `create` — Home, Photos to PDF, Scan, Arrange pages, Options sheet, Name dialog, ID card mode, Done.
- `library` — My PDFs list, rename, delete, details.
- `viewer` — PDF viewer, password prompt.
- `tools` — Tools hub + one screen per tool (all reuse the same pick → options → progress → done pattern).
- `settings` — Settings, About, Licenses.

### `:app`
`MainActivity`, `NavHost`, Hilt `@HiltAndroidApp` class, manifest, and **incoming intent routing**:
- `ACTION_SEND` / `ACTION_SEND_MULTIPLE` with `image/*` → Arrange pages
- `ACTION_SEND` / `ACTION_VIEW` with `application/pdf` → Viewer (with Tools quick menu)

---

## 4. How one screen works (pattern every screen follows)

```kotlin
// 1. State: everything the screen shows
data class ArrangeUiState(
    val pages: List<PageItem> = emptyList(),
    val isWorking: Boolean = false,
    val progress: JobProgress? = null,
    val error: AppError? = null,
)

// 2. Events: everything the user can do
sealed interface ArrangeEvent {
    data class Move(val from: Int, val to: Int) : ArrangeEvent
    data class Rotate(val index: Int) : ArrangeEvent
    data object MakePdf : ArrangeEvent
}

// 3. ViewModel turns events into new state
@HiltViewModel
class ArrangeViewModel @Inject constructor(...) : ViewModel() {
    val state: StateFlow<ArrangeUiState>
    fun onEvent(e: ArrangeEvent) { ... }
}

// 4. Screen draws state and sends events. No logic in the screen.
@Composable fun ArrangeScreen(state: ArrangeUiState, onEvent: (ArrangeEvent) -> Unit)
```
Keep a stateless `ArrangeScreen(...)` (for previews and tests) and a small `ArrangeRoute(...)` that connects it to the ViewModel.

---

## 5. Files and storage (important for Play Store)

| What | Where | How |
|---|---|---|
| PDFs the app makes | `Documents/PDF Banao/` | MediaStore on Android 10+. On Android 8–9 use `WRITE_EXTERNAL_STORAGE` with `maxSdkVersion="28"` |
| "Save as" | Wherever user picks | `ActivityResultContracts.CreateDocument("application/pdf")` |
| Photos from PDF to photos | `Pictures/PDF Banao/<name>/` | MediaStore |
| PDFs user opens from elsewhere | Stay where they are | `OpenDocument` + `takePersistableUriPermission` so we can show them later in My PDFs |
| Work-in-progress files | `cacheDir/work/<jobId>/` | Deleted after job and on every app start |

**Room table `documents`:** `id, uri, displayName, sizeBytes, pageCount, createdAt, lastOpenedAt, source (CREATED/IMPORTED), isLocked`.
On opening My PDFs, quietly check each URI still exists; remove rows whose file is gone.

**Never** request `MANAGE_EXTERNAL_STORAGE` or `READ_MEDIA_IMAGES` — the Photo Picker and SAF cover everything.

---

## 6. Heavy work (making, joining, compressing)

- All PDF and image work runs on `Dispatchers.Default` / `Dispatchers.IO`, never on the main thread.
- `JobRunner` (singleton, app-wide coroutine scope) runs **one heavy job at a time** — cheap phones can't do two.
- Jobs report `JobProgress` so screens show "Page 7 of 30".
- Jobs can be cancelled (Cancel button). On cancel, delete partial output.
- Write to a temp file first, then move/copy to the final place only when complete → no half-written PDFs.
- If a job may run long (big merge/compress), show a notification so Android doesn't kill it when the user switches apps (foreground service of type `dataSync` or `shortService` — decide in Milestone 4 and record the choice in `docs/decisions/`).

### Memory rules (the #1 cause of crashes in this kind of app)
1. Process **one page at a time**. Never keep all page bitmaps in memory.
2. Decode with `inSampleSize` / `ImageDecoder.setTargetSize` to the size actually needed.
3. Target pixel sizes for A4 pages:
   - High ≈ 2480 × 3508 (300 DPI), JPEG quality 85
   - Medium ≈ 1654 × 2339 (200 DPI), JPEG quality 75
   - Small ≈ 1240 × 1754 (150 DPI), JPEG quality 60
4. `bitmap.recycle()` after each page is written.
5. Thumbnails: render small (≈ 300 px wide) and let Coil cache them.

### Hitting a target size ("under 200 KB")
1. Estimate bytes per page from the target.
2. Try Medium settings → measure size.
3. If too big: lower JPEG quality step by step, then lower resolution step by step (a simple binary search, max ~6 tries).
4. If still too big at the lowest acceptable setting, stop and tell the user the smallest size possible.

### Making an existing PDF smaller
- **Normal:** with PdfBox, find images inside the PDF and re-save them as smaller JPEGs. Text stays as text.
- **Strong (fallback):** render each page as an image with `PdfRenderer` and build a new PDF from those images. Much smaller, but text becomes an image → show the note from PRD F11.

---

## 7. Errors

- Core functions throw or return clear errors; ViewModels map them to `AppError`:
  `StorageFull`, `FileNotFound`, `WrongPassword`, `FileLocked`, `NotAPdf`, `TooLargeForPhone`, `TargetSizeNotReached(smallestBytes)`, `ScannerUnavailable`, `Unknown`.
- Each `AppError` maps to one simple message in `strings.xml` that says what to do next.
- Never show raw exception text to users. Log it with `Log.e` in debug builds only.

---

## 8. Privacy and security

- **Goal: no `INTERNET` permission.** ML Kit's scanner is downloaded by Google Play services, not by our app — verify in Milestone 2 that scanning still works without the permission. If it doesn't, keep the permission, but send no data anywhere.
- No analytics or crash SDKs. Use Play Console's Android vitals for crash reports (needs no SDK).
- Passwords are never stored or logged.
- Play Store "Data safety" form: **No data collected, no data shared.**
- A short privacy policy page is still required by Play — host it on GitHub Pages.

---

## 9. Testing

| Kind | What | Where |
|---|---|---|
| Unit tests (JVM) | ViewModels with fake repositories, filename cleanup, target-size search logic | `src/test` in each module |
| Instrumented tests | PdfBox + PdfRenderer on real Android: make PDF from sample images, merge, split, lock/unlock, compress | `core/pdf/src/androidTest` with small sample files in `androidTest/assets` |
| Compose UI tests | Main flow: pick → arrange → name → done | `feature/create/src/androidTest` |
| Manual checks | Low-end emulator (2 GB RAM, Android 8), font size 200%, Hindi, dark mode, TalkBack | Checklist in ROADMAP Milestone 5 |

Rule: **no feature is "done" until its tests pass in CI.**

---

## 10. Release setup

- Build **Android App Bundle** (`.aab`), R8 shrinking + resource shrinking on in release.
- PdfBox needs ProGuard keep/dontwarn rules — add them and test the release build, not just debug.
- Baseline Profile for faster start-up.
- Signing key stored outside the repo (never commit `keystore` or passwords). Use Play App Signing.

---

## 11. Decision records

When an important choice is made during building (e.g. "foreground service type", "crop library"), Claude Code writes a short file in `docs/decisions/NNN-title.md`:
```
# 003 — Crop library
Decision: ...
Why: ...
Other options we looked at: ...
```
This stops future sessions from re-arguing old decisions.
