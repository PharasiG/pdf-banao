# 001 — PDF engine

Decided in task 0.4 (19 Oct 2026).

Decision: **PdfBox-Android** makes our PDFs, with every photo stored inside the PDF as a JPEG.
Android's built-in `PdfDocument` is not used. (Showing PDFs on screen stays with Android's `PdfRenderer`.)

## The test
`core/pdf/src/androidTest/.../PdfEngineSpikeTest.kt` makes the same 10-photo PDF both ways:
- **PdfBox:** each photo is turned into a JPEG (quality 75, our Medium setting) and put on the page.
- **`PdfDocument`:** each photo is drawn on the page's canvas, the only way this Android tool works.

Same input for both: the 4 sample photos in `core/pdf/src/androidTest/assets/` (see `SOURCES.md` there),
repeated to make 10 pages. Together the pages hold 12.2 million pixels (about 1.2 MP per page).
Pages are A4, turned sideways for landscape photos, with an 18-point (about 6 mm) margin.
Each way ran once to warm up, then 3 more times. The time below is the middle of those 3.

Run it again with `./gradlew :core:pdf:connectedDebugAndroidTest`, then read the numbers with
`adb logcat -d -s PdfEngineSpike`. The test fails if PdfBox's file is not the smaller one.

## Results
On the owner's phone (OnePlus CPH2585, Snapdragon 8 Gen 2, 16 GB RAM, Android 14). Two runs gave the same sizes, and times within 15 ms.

| | File size | Time | Memory held until the file is saved |
|---|---|---|---|
| PdfBox (JPEG) | **1,889 KB** (about 190 KB a page) | **308 ms** | **2 MB** |
| Android `PdfDocument` | 18,589 KB (about 1.9 MB a page) | 1,647 ms | 48 MB |

PdfBox's file is **9.8 times smaller** and it is about **5 times faster**.

## Why
- **File size.** Looking inside the files: PdfBox stores each photo as a JPEG. `PdfDocument` stores the raw
  pixels and only squeezes them the lossless (ZIP-style) way, which doesn't work well for photos. `PdfDocument`
  has no option to use JPEG instead. Small files matter most for our users: WhatsApp, email and government
  upload forms (often "under 200 KB") all need them.
- **Memory.** `PdfDocument` keeps the full pixels of **every page** in memory until it saves the file.
  The 48 MB above is almost all photo pixels: 12.2 million pixels × 4 bytes is 47 MB.
  At Medium quality, a phone photo is up to about 3.6 million pixels a page, so 30 photos would need
  about 430 MB, enough to crash a cheap phone.
  That breaks our "one page at a time" rule (ARCHITECTURE §6). PdfBox only keeps the small JPEGs.
- **Speed.** PdfBox was faster too. The times will be slower on a cheap phone, but the gap should stay.
- **What real files may look like (rough guess):** our sample pages are smaller than real Medium pages.
  A real 3.6 MP Medium page would be about 3 times our average page, so roughly 0.5 MB a page with PdfBox.

## Other options we looked at
- **Android `PdfDocument`:** built in (adds nothing to the app's size), but the files are about 10 times
  bigger, it is slower, and it uses a lot of memory (see above).
- **iText:** AGPL license. Not allowed (CLAUDE.md).
- **Apache PDFBox (the original):** needs desktop Java parts (`java.awt`) that Android doesn't have.
  PdfBox-Android is the Android version of it.

## Notes for later tasks
- **Old but stable.** PdfBox-Android 2.0.27.0 (January 2023) is still the newest version. The project's last
  code change was in December 2023. It is not archived and it is Apache 2.0. Because the app only uses it
  behind our own `PdfCreator` / `PdfTools` interfaces (ARCHITECTURE §3), it can be replaced later if needed.
- **Task 1.3** makes PdfBox an `implementation` dependency of `:core:pdf`. Right now it is used only by the
  test (`androidTestImplementation`), so the app doesn't include it yet. Call
  `PDFBoxResourceLoader.init(context)` once at app start, as the test does in `setUp()`.
- **Task 1.3:** by default PdfBox keeps the whole new PDF in memory until it saves (about 0.5 MB a page at
  Medium). For 100 pages that is about 50 MB, which is too much for a cheap phone. Try
  `PDDocument(MemoryUsageSetting.setupTempFileOnly())`, which keeps the pages in a temp file instead, and
  measure it.
- **Lock / Remove password:** PdfBox brings BouncyCastle 1.72 (2022) for passwords. The newest is 1.86.
  When those tasks start, try the newest BouncyCastle (set its version in `libs.versions.toml`) and test lock and unlock.
- **Task 6.1** adds PdfBox's R8 rules and checks how much it adds to the app's download size. This test didn't measure that.
- EXIF rotation is not fixed in this test (`photo_exif_rotate90.jpg` comes out sideways). Task 1.2's `ImageProcessor` does that.
