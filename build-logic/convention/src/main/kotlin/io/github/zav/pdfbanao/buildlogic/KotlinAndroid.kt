package io.github.zav.pdfbanao.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion

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
