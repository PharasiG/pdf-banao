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
}
