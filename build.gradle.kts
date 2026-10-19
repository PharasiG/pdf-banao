buildscript {
    dependencies {
        // AGP 9 has Kotlin built in, but it ships an older Kotlin version.
        // This makes the whole build use the Kotlin version from libs.versions.toml.
        classpath(libs.kotlin.gradlePlugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false

    alias(libs.plugins.detekt)
    alias(libs.plugins.spotless)
}

// Code checks. Each tool is set up once, here, and covers every module and build-logic/.
// See docs/decisions/002-code-quality-and-ci.md.

// The files to check. Folders that builds write to are skipped completely: looking inside them
// fails when another task is writing there at the same time.
fun projectFiles(vararg patterns: String) = fileTree(rootDir) {
    include(*patterns)
    exclude("**/build/**", "**/.gradle/**", "**/.kotlin/**", ".idea/**")
}

// ktlint's style settings. They are here, not in .editorconfig, because Spotless does not notice
// changes to .editorconfig and would keep using old settings. The ij_ ones are also in .editorconfig
// for Android Studio's "Reformat Code": keep both places the same.
val ktlintStyle = mapOf(
    // Same style as Android Studio's "Reformat Code". Lines up to 100 characters.
    "ktlint_code_style" to "android_studio",
    // Keep the comma after the last item of a multi-line list. Adding an item then changes one line.
    "ij_kotlin_allow_trailing_comma" to true,
    "ij_kotlin_allow_trailing_comma_on_call_site" to true,
    // Imports in plain A-Z order.
    "ij_kotlin_imports_layout" to "*",
    // Compose screens are functions whose names start with a capital letter, like HomeScreen().
    "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
    // One parameter per line when a function has 2 or more, or a class has 1 or more (like UiState).
    "ktlint_function_signature_rule_force_multiline_when_parameter_count_greater_or_equal_than" to
        2,
    "ktlint_class_signature_rule_force_multiline_when_parameter_count_greater_or_equal_than" to 1,
)

// Formatting. `./gradlew spotlessApply` fixes it, `./gradlew spotlessCheck` only checks.
spotless {
    // Same as .gitattributes. Spotless's default (asking git per file) breaks on locked Gradle files.
    lineEndings = com.diffplug.spotless.LineEnding.UNIX

    kotlin {
        target(projectFiles("**/src/**/*.kt"))
        val composeRules = libs.compose.rules.ktlint.get()
        ktlint(libs.versions.ktlint.get())
            .editorConfigOverride(ktlintStyle)
            .customRuleSets(listOf("${composeRules.module}:${composeRules.version}"))
    }
    kotlinGradle {
        target(projectFiles("**/*.gradle.kts"))
        ktlint(libs.versions.ktlint.get()).editorConfigOverride(ktlintStyle)
    }
    format("xml") {
        target(projectFiles("**/src/**/*.xml"))
        trimTrailingWhitespace()
        endWithNewline()
    }
    format("misc") {
        target(
            projectFiles(
                "**/*.md",
                "**/*.toml",
                "**/*.yml",
                ".editorconfig",
                ".gitattributes",
                ".gitignore",
            ),
        )
        trimTrailingWhitespace()
        endWithNewline()
    }
}

// Finds risky or messy Kotlin code. Our changes to the default rules are in config/detekt/detekt.yml.
detekt {
    source.setFrom(projectFiles("**/src/**/*.kt"))
    config.setFrom("config/detekt/detekt.yml")
    buildUponDefaultConfig = true
    parallel = true
}
