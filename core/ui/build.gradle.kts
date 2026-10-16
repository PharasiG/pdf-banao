plugins {
    alias(libs.plugins.pdfbanao.android.library)
    alias(libs.plugins.pdfbanao.android.library.compose)
}

android {
    namespace = "io.github.zav.pdfbanao.core.ui"
}

dependencies {
    api(libs.androidx.compose.material3)
}
