package io.github.zav.pdfbanao.buildlogic

/** Android SDK levels for the whole app. See docs/decisions/000-project-setup.md. */
internal object PdfBanaoSdk {
    /** The SDK we build against. The newest AndroidX libraries need 37. */
    const val COMPILE = 37

    /** The Android version the app is tested for. Google Play requires 36. */
    const val TARGET = 36

    /** Android 8.0 */
    const val MIN = 26
}
