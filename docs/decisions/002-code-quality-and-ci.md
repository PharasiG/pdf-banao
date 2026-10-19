# 002 — Code checks and CI

Decided in task 0.2 (17 Oct 2026).

## One setup for all modules
Decision: Spotless (code layout) and Detekt (risky code) are set up once, in the root `build.gradle.kts`.
They check every `src/` folder in the project, `build-logic/` included. Android Lint is set up in the shared
recipes in `build-logic/` (`AndroidLint.kt`), because Lint has to run inside each Android module.
Why: One place to read and change. A new module is checked automatically, and nobody has to remember to add it.
This is not a `subprojects {}` block (see 000): the root build only reads files, it doesn't change other modules.
Other options we looked at: adding Spotless and Detekt to each module through the `build-logic` recipes.
That means more code, and `build-logic/` itself would need its own setup.

## Code layout: ktlint through Spotless
Decision: ktlint 1.8.0, run by Spotless. The style is `android_studio` (the same as Android Studio's "Reformat Code",
lines up to 100 characters), with three changes:
- Keep the comma after the last item in a multi-line list. Adding an item then changes only one line.
- One parameter per line when a function has 2 or more parameters, or a class has 1 or more. This is how our screens
  and `UiState` classes were already written (see ARCHITECTURE §4).
- Imports in plain A–Z order.

The settings are in `ktlintStyle` in the root `build.gradle.kts`, **not** in `.editorconfig`.
Why: while setting this up, we found that Spotless doesn't notice when `.editorconfig` changes. It kept using
the old settings, even after restarting Gradle. Settings written in the build file are always picked up.
`.editorconfig` still has the few settings Android Studio needs (trailing commas, import order). Keep those the same as `ktlintStyle`.

`spotlessApply` fixes most problems by itself. A few, like a line that is too long, must be fixed by hand.

## Extra rules for Compose screens
Decision: Compose rules (`io.nlopez.compose.rules:ktlint`), run together with ktlint.
Why: They catch common Compose mistakes, for example a screen without a `modifier` parameter.
They also push for the stateless `Screen` pattern from ARCHITECTURE §4. The ktlint version needs no extra setup.

## Detekt: alpha version on purpose
Decision: Detekt **2.0.0-alpha.6**, an exception to the "latest stable version" rule. The owner approved this in task 0.2.
Why: the last stable Detekt (1.23.8, Feb 2025) was built for Kotlin 2.0, and we use Kotlin 2.4.
It also crashes when Gradle runs on Java 25, and Java 25 is the Java built into Android Studio
([detekt issue 8714](https://github.com/detekt/detekt/issues/8714)). The Detekt team said they won't fix this in 1.x.
2.0.0-alpha.6 is built for exactly our setup: Gradle 9.6, Kotlin 2.4, AGP 9, Java 25.
**When Detekt 2.0.0 becomes stable, switch to it** (change `detekt` in `libs.versions.toml`).
Alpha versions can rename rules or settings. Detekt checks `config/detekt/detekt.yml` and says clearly if a name is wrong.
Detekt runs without "type resolution" (the lighter mode), which is faster and needs less memory.
Our changes to the default rules are in `config/detekt/detekt.yml`, each with a reason.
Other options we looked at: skip Detekt until 2.0 is stable; run 1.23.8 on an older Java (more setup, and it still reads Kotlin as 2.0).

## Android Lint
Decision: Lint warnings fail the build, the same as errors. We turned off three checks that look online for
newer versions of Gradle and libraries (`AndroidGradlePluginVersion`, `GradleDependency`, `NewerVersionAvailable`).
Why: problems are easier to fix while they are small. The version checks give a different answer every time a
new library comes out, so CI could go red even when our code didn't change. We update versions on purpose instead.
(This also hides the "newer Gradle available" reminder from 000. Updating Gradle is still a to-do.)

## CI (GitHub Actions)
Decision: `.github/workflows/ci.yml` runs on every push and every pull request:
`./gradlew spotlessCheck detekt lintDebug testDebugUnitTest assembleDebug`, the same command as on your computer.
- Ubuntu, Java 25 (Temurin), the same Java version as Android Studio.
- Gradle's **basic** cache (`cache-provider: basic`). Why: it's open source and free for private repositories too.
  The default "enhanced" cache uses Gradle's closed-source service, and is free only for public repositories.
- The workflow can only read the repository (`permissions: contents: read`).
- If something fails, the reports are saved with the run for 14 days (download them from the run's page).
- A newer push to the same branch stops the older run.
- The Gradle setup step also checks that `gradle-wrapper.jar` is the real file from Gradle.
- Actions use version tags (`@v7`), not exact commit IDs, so the file stays easy to read.

## Line endings
Decision: `.gitattributes` makes every text file use LF line endings, except Windows `.bat` scripts (CRLF).
Spotless also writes LF.
Why: the owner works on Windows and CI runs on Linux. Without this, the same file can look different on the two
machines, and checks or scripts like `gradlew` can fail on one of them.
