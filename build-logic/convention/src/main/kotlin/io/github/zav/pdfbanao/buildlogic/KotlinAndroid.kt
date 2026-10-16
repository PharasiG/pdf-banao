package io.github.zav.pdfbanao.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion

/** Android SDK levels for the whole app. See docs/decisions/000-project-setup.md. */
internal object PdfBanaoSdk {
    /** The SDK we build against. The newest AndroidX libraries need 37. */
    const val COMPILE = 37

    /** The Android version the app is tested for. Google Play requires 36. */
    const val TARGET = 36

    /** Android 8.0 */
    const val MIN = 26
}

/** Setup shared by every Android module (app and libraries). */
internal fun configureKotlinAndroid(commonExtension: CommonExtension) {
    commonExtension.apply {
        compileSdk = PdfBanaoSdk.COMPILE

        defaultConfig.apply {
            minSdk = PdfBanaoSdk.MIN
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
}
