plugins {
    alias(libs.plugins.pdfbanao.android.feature)
}

android {
    namespace = "io.github.zav.pdfbanao.feature.create"
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.coil.compose)
}
