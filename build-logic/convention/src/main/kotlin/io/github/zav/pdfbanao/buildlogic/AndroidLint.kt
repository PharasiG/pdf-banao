package io.github.zav.pdfbanao.buildlogic

import com.android.build.api.dsl.CommonExtension

/** Android Lint settings for every Android module. See docs/decisions/002-code-quality-and-ci.md. */
internal fun configureAndroidLint(commonExtension: CommonExtension) {
    commonExtension.lint.apply {
        // Any problem fails the build, so problems get fixed while they are still small.
        abortOnError = true
        warningsAsErrors = true

        // These checks look online for newer versions of Gradle and libraries. Their answer changes
        // from day to day, so CI could fail without any change in our code. We update versions on purpose.
        disable += setOf("AndroidGradlePluginVersion", "GradleDependency", "NewerVersionAvailable")
    }
}
