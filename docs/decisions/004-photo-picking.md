# 004 — Picking photos

Decided in task 1.1 (19 Oct 2026).

## Android's photo picker, opened straight from Home
Decision: tapping **Photos to PDF** opens Android's photo picker (`ActivityResultContracts.PickMultipleVisualMedia`)
right away, on top of Home. It shows photos only (no videos) and allows up to 100. When the user taps "Add", the
**Selected photos** screen opens with the photos. If the user closes the picker without picking, they stay on Home.
How it works: `HomeViewModel` turns the button tap into `openPhotoPicker = true`, the same way other buttons set
`showComingSoon` (see 003). `HomeRoute` opens the picker and sends `PhotoPickerOpened`.
Why:
- One tap fewer, and nothing to close if the user changes their mind (PRD §3: "3 taps to a PDF").
- No storage permission. The app can read only the photos the user picked (ARCHITECTURE §5).
- Scan with camera (task 2.4) can work the same way: open the scanner from Home, then show the pages.
Other options we looked at: open an empty Selected photos screen first and let it open the picker by itself.
The empty screen flashes before the picker, and closing the picker means closing that screen again.

## The order and the 100-photo limit
- `isOrderedSelection = true`: on newer Android versions the picker puts a number on each photo as the user taps
  it (PRD F1: "in the order the user tapped them"). Older pickers ignore it.
- 100 is the photo picker's own maximum (`MediaStore.getPickImagesMaxLimit()`), so it can't be asked for more.
  The number is `MAX_PHOTOS` in `feature/create/.../photos/PhotoLimits.kt`.
- On phones that have no photo picker (some Android 8–10 phones without Google Play's copy of it), androidx falls
  back to Android's file picker, which has **no limit**. So `SelectedPhotosViewModel` keeps the first 100 and the
  screen shows a short note saying so (`someLeftOut`).

## The photo list travels with the navigation step
Decision: the Selected photos screen's address is `SelectedPhotosDestination(photoUris: List<String>)`.
The ViewModel gets the list from Hilt when it is made ("assisted injection":
`@HiltViewModel(assistedFactory = ...)` + `hiltViewModel<ViewModel, Factory> { it.create(photoUris) }`).
Why:
- Navigation saves the list with the screen. It comes back after turning the phone, and after Android closes the
  app in the background. 100 photo addresses are about 10 KB, which is fine.
- ViewModel tests just call `SelectedPhotosViewModel(listOf(...))`. Reading the route inside the ViewModel
  (`SavedStateHandle.toRoute()`) would need Robolectric (a fake Android) in plain unit tests.
- Later, photos shared from other apps (task 2.5) can open the same screen with their own list.
- Addresses are `String`, not `android.net.Uri`, because `Uri` doesn't work in plain JVM unit tests.
  Coil and Android's `ContentResolver` accept them as text.

Other options we looked at: an app-wide in-memory holder for the photos (lost when Android closes the app in the
background); reading the route in the ViewModel (needs Robolectric, see above).

## Small pictures: Coil 3
Decision: `io.coil-kt.coil3:coil-compose` (the image loader ARCHITECTURE §2 already chose). Only that part:
no `coil-network-*`, so the app still has **no internet permission** (checked in the final manifest).
Why: Coil loads each photo at the size it is shown (80 dp), not at full size, so 100 photos fit in a cheap phone's
memory. It turns sideways photos the right way up using EXIF. It is small and made for Compose.

## Events that only move to another screen
Decision: a tap that only goes somewhere else (like the back arrow, `SelectedPhotosEvent.BackClick`) is still an
`Event`, so the `Screen` stays simple and testable. The `Route` turns it into a navigation call; the ViewModel isn't
told. The Route wraps it in `dropUnlessResumed`, so a quick double tap on back doesn't also close Home.

## Known limit: photo access after Android closes the app
Android's documentation says the photo picker's access lasts until the phone restarts or the app stops. If Android
closes the app in the background while the list is open, the addresses come back but the photos may not open,
so the pictures show as empty boxes. **Task 1.3 or 2.1 should fix this**, for example with
`ContentResolver.takePersistableUriPermission` (and giving the access back when the PDF is made), or by copying
the photos into the job's work folder.
