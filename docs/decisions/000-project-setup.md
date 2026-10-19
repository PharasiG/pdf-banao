# 000 — Project setup

Decided in task 0.1 (16 Oct 2026). Number 001 is kept for the PDF engine decision (task 0.4).

## App ID
Decision: `io.github.zav.pdfbanao` (owner's GitHub username: `Zav`, written in lowercase).
Why: GitHub usernames are unique, so the ID is unique. It can never change after the first Play Store upload.

## SDK levels
Decision: `minSdk = 26`, `targetSdk = 36`, **`compileSdk = 37`**.
Why: The newest stable AndroidX libraries (Compose 1.12, core 1.19, Hilt ViewModel helper 1.4) only build with SDK 37.
`compileSdk` only decides which tools we build with. It doesn't change how the app behaves on phones.
`targetSdk` is what Google Play checks, and it stays at 36.
Other options we looked at: compileSdk 36 with older library versions. Rejected because it breaks the "latest stable" rule.

## Shared build recipes (`build-logic/`)
Decision: Setup that every module shares lives in small Gradle "convention plugins" in `build-logic/`:
`pdfbanao.android.application`, `pdfbanao.android.library`, `pdfbanao.android.library.compose`,
`pdfbanao.android.feature`, `pdfbanao.hilt`. Each module's `build.gradle.kts` is then only a few lines.
Why: There are 11 modules. Without shared recipes, each would repeat the same SDK, Java and Compose setup.
The feature recipe also **fails the build if a feature depends on another feature** (ARCHITECTURE §1).
Other options we looked at: copying the setup into each module (easy to drift apart); `subprojects {}` blocks in the root build file (Gradle discourages this).

## Kotlin version
Decision: Use AGP 9's built-in Kotlin (no `org.jetbrains.kotlin.android` plugin). The root `build.gradle.kts` puts the
Kotlin Gradle plugin from `libs.versions.toml` on the classpath, so the build uses that Kotlin version, not the older one AGP brings.
Why: This is how AGP 9 expects Kotlin to be set up, and it keeps one Kotlin version for the whole build.

## Other small choices
- `android:allowBackup="false"` + `data_extraction_rules.xml`: Android won't copy the app's data (like the My PDFs list) to Google Drive backups. Fits "files never leave your phone". Moving data phone-to-phone is still allowed.
- Gradle wrapper is 9.6.0 (what AGP 9.4.1 recommends), not the newest 9.8.1: on the owner's connection, downloads from GitHub's file servers (where Gradle is hosted) ran at about 5 KB/s, while 9.6.0 was already on the computer. Update it later with `./gradlew wrapper --gradle-version <newest>` when the download works. Lint showed a reminder warning until task 0.2 turned that check off (see 002).
- `:core:model` is an Android library, not plain Kotlin, so it can use Android types like `Uri` later.
- `HomeUiState` has a placeholder `isLoading` field because a Kotlin data class needs at least one field. Task 0.3/3.1 replaces it with real content.
