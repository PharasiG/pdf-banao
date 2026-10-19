# PDF Banao (PDF बनाओ)

[![CI](https://github.com/Zav/pdf-banao/actions/workflows/ci.yml/badge.svg)](https://github.com/Zav/pdf-banao/actions/workflows/ci.yml)

A free Android app that turns photos into PDFs and does everyday PDF jobs: make a PDF smaller, join, split, lock, and view.
Made for Indian smartphone users, in English and Hindi.

**Photos to PDF in 3 taps. Free. No ads. Works offline. Your files never leave your phone.**

> **Status:** early development. Not on Google Play yet.

## Privacy

- No internet permission, no ads, no analytics, no account.
- Your files stay on your phone. The app sends nothing anywhere.

## What it will do (version 1.0)

- **Make PDFs:** Photos to PDF, Scan with camera, ID card (front + back on one page)
- **Arrange pages:** reorder, rotate, crop, black & white
- **My PDFs:** list, view, rename, share, delete
- **Tools:** Make PDF smaller, Join PDFs, Split / Take out pages, Arrange pages of a PDF, Lock / Unlock, PDF to photos
- **Languages:** English and Hindi

The full list is in [docs/PRD.md](docs/PRD.md).

## Build it yourself

You need [Android Studio](https://developer.android.com/studio) (latest stable). It comes with the Java version the build uses.

1. Open this folder in Android Studio.
2. Press **Run** to install the app on an emulator or phone (Android 8.0 or newer).

Or build from a terminal:

```bash
./gradlew assembleDebug
```

## Checks

Run these before every commit. GitHub runs the same checks on every push and pull request ([`.github/workflows/ci.yml`](.github/workflows/ci.yml)).

```bash
./gradlew spotlessApply
```

```bash
./gradlew spotlessCheck detekt lintDebug testDebugUnitTest assembleDebug
```

| Command | What it does |
|---|---|
| `spotlessApply` | Fixes the code layout (spaces, line breaks, import order) by itself |
| `spotlessCheck` | Checks the code layout, plus extra rules for Compose screens |
| `detekt` | Finds risky or messy Kotlin code |
| `lintDebug` | Android's own checks: layouts, resources, permissions, and more |
| `testDebugUnitTest` | Runs the unit tests |
| `assembleDebug` | Builds the app |

If a check fails, its report is in `build/reports/` (Detekt) or `<module>/build/reports/` (Lint, tests).
How the checks are set up, and why: [docs/decisions/002-code-quality-and-ci.md](docs/decisions/002-code-quality-and-ci.md).

## How the code is organised

| Module | What's in it |
|---|---|
| `:app` | Starts the app, moves between screens |
| `:feature:*` | The screens: `create`, `library`, `viewer`, `tools`, `settings` |
| `:core:*` | The work behind the screens: `pdf`, `image`, `data`, `ui`, `model` |
| `build-logic/` | Build setup shared by all modules |

Screens only talk to `:core` modules, never to each other. More in [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Project documents

- [docs/PRD.md](docs/PRD.md): what we're building, and for whom
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md): how it's built
- [docs/ROADMAP.md](docs/ROADMAP.md): the task list
- [docs/decisions/](docs/decisions/): important choices and why we made them

## License

Copyright 2026 Zav. Licensed under the [Apache License 2.0](LICENSE).
