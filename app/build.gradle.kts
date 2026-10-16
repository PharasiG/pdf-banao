plugins {
    alias(libs.plugins.pdfbanao.android.application)
    alias(libs.plugins.pdfbanao.hilt)
}

android {
    namespace = "io.github.zav.pdfbanao"

    defaultConfig {
        // Never change this after the first Play Store upload.
        applicationId = "io.github.zav.pdfbanao"
        versionCode = 1
        versionName = "0.1.0"
    }
}

dependencies {
    implementation(project(":core:ui"))

    implementation(project(":feature:create"))
    implementation(project(":feature:library"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:tools"))
    implementation(project(":feature:viewer"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
}
