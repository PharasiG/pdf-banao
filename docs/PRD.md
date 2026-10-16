# PDF Banao — Product Requirements (PRD)

> Put this file at `docs/PRD.md`.
> This file says **what** we are building and **for whom**. `ARCHITECTURE.md` says **how**.

---

## 1. What is PDF Banao?

A free Android app that makes PDFs from photos in a few taps, and does the everyday PDF jobs people need: make smaller, join, split, lock, share.

**One-line promise:** *Photos to PDF in 3 taps. Free. No ads. Works offline. Your files never leave your phone.*

---

## 2. Who is it for?

Indian smartphone users, age 25–55. Many are not "tech" people. They use WhatsApp every day, and they need PDFs for real-life paperwork.

| Person | Situation | What they need |
|---|---|---|
| **Priya, 27** — job seeker | Uploading marksheets, Aadhaar, photo to a job or exam portal. Portal says "PDF under 200 KB". | Make PDF, make it small enough, rename it properly. |
| **Ramesh, 48** — shop owner | Sends bills and stock lists to suppliers on WhatsApp. | Fast: photos → PDF → WhatsApp. Big buttons, Hindi. |
| **Sunita, 38** — parent / homemaker | School forms, bank KYC, gas connection, insurance papers. | Scan with camera, put Aadhaar front + back on one page. |
| **Arun, 52** — govt. employee | Gets PDFs on WhatsApp/email, needs to join them or send only some pages. | Merge, extract pages, lock with password. |

**Things to remember about these users:**
- Many phones are low-cost (2–4 GB RAM, Android 8–12, little free storage).
- Many people use Hindi or another Indian language. Many read English but prefer simple words.
- Some have weak eyesight or use large font size. Buttons must be big and clear.
- Mobile data can be costly or slow. The app must work fully offline.
- Trust matters. People are scanning Aadhaar, PAN, bank papers. They worry about where files go.

---

## 3. Product principles (use these to decide anything not written here)

1. **Simple first.** If a feature makes the main flow harder, hide it under "More options".
2. **3 taps to a PDF.** Open app → pick photos → Done → share. Everything else is optional.
3. **Say it simply.** "Make PDF smaller", not "Compress". "Join PDFs", not "Merge". Always icon **plus** text label.
4. **Private by design.** No login, no ads, no analytics, no internet use. Files stay on the phone.
5. **Kind to cheap phones.** Small app size, no crashes on 50 photos, no freezing screens.
6. **Never lose the user's work.** Confirm before delete, undo where possible, never overwrite the original file without asking.
7. **Show file size everywhere.** Users care about "will this upload / send?".

---

## 4. Scope

### 4.1 Launch (v1.0) — must have

| # | Feature | Why |
|---|---|---|
| F1 | **Photos to PDF** (from gallery) | Main purpose of the app |
| F2 | **Scan with camera** | People often have paper, not photos |
| F3 | **Arrange pages** (reorder, rotate, crop, delete, add more) | Photos are often in wrong order or sideways |
| F4 | **PDF options** (page size, margins, quality) | Control size and look |
| F5 | **Name, Save, Save as** | Core flow |
| F6 | **Done screen** (Open, Share, WhatsApp, Print, Save as) | Core flow |
| F7 | **Receive photos from other apps** ("Share → PDF Banao") | Very common: photos come from WhatsApp |
| F8 | **ID card mode** (front + back on one page) | Aadhaar, PAN, DL, voter ID — very common need in India |
| F9 | **My PDFs** (list, search, sort, rename, delete, share) | Find files again |
| F10 | **PDF viewer** + "Open with PDF Banao" | Check the PDF before sending |
| F11 | **Make PDF smaller** (with target size like "under 200 KB") | Govt/exam/job portals have size limits |
| F12 | **Join PDFs** (merge) | Common need |
| F13 | **Split / Take out pages** | Send only needed pages |
| F14 | **Arrange pages of a PDF** (reorder, rotate, delete) | Fix a PDF received from someone |
| F15 | **Lock / Unlock PDF** (password) | Bank and personal documents |
| F16 | **PDF to photos** | Send a page as image on WhatsApp |
| F17 | **Languages:** English + Hindi | Main audience |
| F18 | **Settings** (language, theme, default quality, default page size, save folder info) | Basic control |

### 4.2 After launch (v1.x) — nice to have
- More languages: Bengali, Marathi, Telugu, Tamil, Gujarati, Kannada, Malayalam, Punjabi, Odia
- Add page numbers
- Add watermark text
- Add signature (draw once, place on page)
- Copy text from scanned PDF (OCR — ML Kit supports Devanagari and Latin)
- Photo tools (make photo smaller / resize — for portals that need a JPG under X KB)
- Home-screen shortcuts ("Scan", "Photos to PDF")

### 4.3 Not doing (non-goals)
- No user accounts, no cloud storage, no sync
- No ads, no in-app purchases (app is free)
- No editing of PDF text
- No Word/Excel/PowerPoint conversion
- No "All files access" permission (Play Store restricts it, and we don't need it)

---

## 5. Screen map

```
Home
 ├─ [Photos to PDF]  → Photo picker → Arrange pages → (Options) → Name → Making… → Done
 ├─ [Scan with camera] → Scanner → Arrange pages → … → Done
 ├─ [ID card]        → Pick/scan front → Pick/scan back → Preview → Name → Done
 ├─ [My PDFs]        → List → Viewer / Rename / Share / Delete / Details
 └─ Tools (grid)
      ├─ Make PDF smaller
      ├─ Join PDFs
      ├─ Split / Take out pages
      ├─ Arrange pages of a PDF
      ├─ Lock PDF / Unlock PDF
      └─ PDF to photos
Settings (top-right icon)
```

**Home screen layout:** three big cards at top (Photos to PDF, Scan, My PDFs), then "Recent PDFs" (last 3), then "Tools" grid with icons and labels. No hamburger menus.

**Every tool follows the same pattern** (so users learn it once):
`Pick file(s) → Choose options (simple, with good defaults) → Big "Start" button → Progress → Done screen`

---

## 6. Feature details and acceptance criteria

"Acceptance criteria" = the checklist that must be true before we call a feature done.

### F1 — Photos to PDF
- Uses the Android **Photo Picker** (no storage permission needed).
- User can select up to **100** photos at once.
- Photos appear in the order the user tapped them.
- Photos taken sideways are shown the right way up (read the photo's EXIF orientation).
- **Done when:** 30 photos from a 2 GB RAM phone become one PDF without crash, in under ~20 seconds, with a progress bar.

### F2 — Scan with camera
- Uses Google's ML Kit Document Scanner (auto edge detection, crop, cleanup filters, multi-page).
- Scanned pages go into the same "Arrange pages" screen as F1.
- If Google Play services is missing/old, show a simple message and offer "Pick from gallery" instead.
- **Done when:** user can scan 5 pages and get one PDF.

### F3 — Arrange pages
- Grid of page thumbnails with page numbers.
- Long-press and drag to reorder.
- Tap a page → Rotate, Crop, Delete, Replace.
- "+ Add more" button (gallery or camera).
- Simple filter per page or all pages: Original / Black & White / Grey (for gallery photos; helps size and readability).
- **Done when:** reorder, rotate, crop, delete all show correctly in the final PDF.

### F4 — PDF options (one bottom sheet, hidden behind "Options", with good defaults)
| Option | Choices | Default |
|---|---|---|
| Page size | A4, Letter, Legal, "Same as photo" | A4 |
| Margin | None, Small, Big | Small |
| Quality | High, Medium, Small file | Medium |
| Target size (optional) | No limit, 100 KB, 200 KB, 500 KB, 1 MB, 2 MB | No limit |

- Show **estimated file size** that updates when options change.
- Orientation is automatic per page (landscape photo → landscape page).
- **Done when:** "Small file" makes a 10-photo PDF clearly smaller than "High", and target size is met or the app clearly says "Could not reach 200 KB. Smallest possible: 260 KB."

### F5 — Name, Save, Save as
- Default name: `PDF_Banao_16-Oct-2026_14-30` style (date + time, no spaces problems).
- Name box opens with the text selected so the user can type a new name directly.
- Bad characters (`/ \ : * ? " < > |`) are removed automatically, silently.
- If a file with the same name exists → add `(1)`, `(2)`.
- **Default save:** phone's `Documents/PDF Banao` folder (visible in the Files app).
- **Save as:** opens the Android "save to" screen so the user can choose any folder (Downloads, Drive, SD card).
- **Done when:** saved file is visible in the phone's Files app and appears in "My PDFs".

### F6 — Done screen
- Big tick, file name, page count, **file size**.
- Buttons: **Share on WhatsApp** (shown only if WhatsApp is installed), **Share**, **Open**, **Save as**, **Print**, **Make another**.
- **Done when:** sharing to WhatsApp sends the PDF as a document (not a link).

### F7 — Receive photos from other apps
- PDF Banao appears in the Android share menu for images (single and multiple).
- Shared photos open directly in "Arrange pages".
- Also: PDF Banao appears in share menu for PDFs → opens a small menu: View, Make smaller, Join with…, Lock.

### F8 — ID card mode
- Step 1: front side (gallery or scan). Step 2: back side.
- Both sides placed on **one A4 page**, top and bottom, real ID-card size (85.6 × 54 mm), centred with space between.
- Option: "Make bigger" (fit width) for easier reading.
- **Done when:** printing the page at 100% gives cards about real size.

### F9 — My PDFs
- Shows PDFs made by the app, and PDFs the user opened/imported in the app.
- Each row: thumbnail of page 1, name, size, pages, date.
- Search by name. Sort by date / name / size.
- Long-press for multi-select → Share, Delete, Join.
- Menu per file: Open, Rename, Share, Save as, Details, Delete.
- Delete asks for confirmation. If the file was deleted outside the app, remove it from the list quietly.
- **Done when:** list stays correct after renaming, deleting, and deleting files from the Files app.

### F10 — PDF viewer
- Smooth scroll, pinch zoom, "page 3 of 12" indicator, jump to page.
- Opens password-locked PDFs after asking for the password.
- Registered as an app that can open PDFs ("Open with PDF Banao").
- Action bar: Share, and a "Tools" button for quick actions on this file.

### F11 — Make PDF smaller
- Choices: **Less smaller (best quality)**, **Medium**, **Much smaller**, or **Under a size**: 100 KB / 200 KB / 500 KB / 1 MB / custom.
- Shows "Before: 4.2 MB → After: 380 KB".
- Saves as a **new file** (`name_small.pdf`). Never replaces the original unless the user chooses to.
- If the strong method turns text into images, show a simple note: "Text in this PDF may not be selectable after making it this small."

### F12 — Join PDFs
- Pick 2 or more PDFs, reorder them by dragging, then Join.
- Can also add photos into the join list (each photo becomes a page).

### F13 — Split / Take out pages
- Show page thumbnails; user taps pages to select.
- Options: "Save selected pages as new PDF", "Make each page a separate PDF", "Split every N pages".

### F14 — Arrange pages of a PDF
- Same screen design as F3 but for an existing PDF: reorder, rotate, delete, add pages from photos.
- Saves as new file by default.

### F15 — Lock / Unlock PDF
- Lock: enter password twice, show/hide toggle. Clear warning: "If you forget this password, nobody can open this file — not even us."
- Unlock: enter current password → save an unlocked copy.
- Uses strong encryption (AES-256).

### F16 — PDF to photos
- Choose all pages or selected pages, quality (normal / high).
- Saves JPGs to `Pictures/PDF Banao/<pdf name>/` so they appear in the Gallery.
- Done screen offers "Share on WhatsApp".

### F17 — Languages
- English and Hindi at launch. Every text on screen must come from string resources — never typed directly in code.
- In-app language switch in Settings (doesn't need to change the whole phone's language).
- Hindi text must be reviewed by a native speaker before release.

### F18 — Settings
- Language, Theme (Light / Dark / Follow phone), Default quality, Default page size, Default margin, "Where files are saved" (info + open folder), About, Privacy policy, Open-source licenses, Send feedback (opens email app).

---

## 7. Words to use (UX writing guide)

| Don't say | Say (English) | Say (Hindi) |
|---|---|---|
| Convert images | Photos to PDF | फ़ोटो से PDF |
| Scan document | Scan with camera | कैमरे से स्कैन करें |
| Compress | Make PDF smaller | PDF छोटा करें |
| Merge | Join PDFs | PDF जोड़ें |
| Split / Extract | Take out pages | पेज अलग करें |
| Encrypt | Lock with password | पासवर्ड से लॉक करें |
| Decrypt | Remove password | पासवर्ड हटाएँ |
| Export | Save as | इस जगह सेव करें |
| Rename | Change name | नाम बदलें |
| Operation failed | Something went wrong. Please try again. | कुछ गड़बड़ हुई। फिर से कोशिश करें। |
| Insufficient storage | Phone storage is full. Delete some files and try again. | फ़ोन की मेमोरी भरी है। कुछ फ़ाइलें हटाकर फिर कोशिश करें। |

Rules:
- Short sentences. No technical words (no "URI", "encode", "DPI", "metadata").
- Error messages always say **what to do next**.
- Keep "PDF", "WhatsApp", "KB", "MB" in English in all languages — people know these words.

---

## 8. Non-functional requirements

| Area | Requirement |
|---|---|
| Phones supported | Android 8.0 (API 26) and newer |
| App size | Download under **10 MB** |
| Speed | App opens in under 2 seconds on a mid-range phone; UI never freezes |
| Memory | No crash with 100 photos of 12 MP on a 2 GB RAM phone |
| Offline | Every feature works with no internet |
| Privacy | No internet permission (if possible), no analytics, no ads, no account |
| Accessibility | Works at 200% font size; all buttons have TalkBack labels; touch targets ≥ 48 dp |
| Stability | Crash-free users ≥ 99.5% (Play Console vitals) |
| Storage | Temporary files are deleted after each job and on app start |

---

## 9. How we know it's working (success measures)

- First-time user makes and shares a PDF in **under 30 seconds** (test with 5 real people from the target group).
- Play Store rating ≥ 4.5.
- Crash-free users ≥ 99.5%.
- Few 1-star reviews mentioning "confusing", "ads", "file too big".

---

## 10. Open questions
- ~~App name~~ — **decided: PDF Banao** (Hindi label: "PDF बनाओ").
- ~~Package ID~~ — **decided: `io.github.zav.pdfbanao`** (unique because GitHub usernames are unique; permanent after first Play upload).
- Who will review Hindi translations?
- Which languages come next after Hindi (decide using Play Console country/language data)?
