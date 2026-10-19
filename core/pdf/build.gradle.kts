plugins {
    alias(libs.plugins.pdfbanao.android.library)
}

android {
    namespace = "io.github.zav.pdfbanao.core.pdf"
}

dependencies {
    // Only the engine test (task 0.4) uses PdfBox so far. Task 1.3 makes it `implementation`
    // for the real PdfCreator, and task 6.1 adds its R8 rules. See docs/decisions/001-pdf-engine.md.
    androidTestImplementation(libs.pdfbox.android)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
}
