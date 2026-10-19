import io.github.zav.pdfbanao.buildlogic.libs
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

/**
 * Recipe for every `:feature:*` module: screens + ViewModels.
 *
 * Features may only use `:core:*` modules. The build fails if a feature
 * depends on another feature (see docs/ARCHITECTURE.md §1).
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("pdfbanao.android.library")
            pluginManager.apply("pdfbanao.android.library.compose")
            pluginManager.apply("pdfbanao.hilt")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            dependencies {
                "implementation"(project(":core:model"))
                "implementation"(project(":core:ui"))

                "implementation"(
                    libs.findLibrary("androidx-hilt-lifecycle-viewmodel-compose").get(),
                )
                "implementation"(libs.findLibrary("androidx-lifecycle-runtime-compose").get())
                "implementation"(libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())
                "implementation"(libs.findLibrary("androidx-navigation-compose").get())
                "implementation"(libs.findLibrary("kotlinx-serialization-core").get())

                "androidTestImplementation"(
                    libs.findLibrary("androidx-compose-ui-test-junit4").get(),
                )
                "androidTestImplementation"(libs.findLibrary("androidx-test-ext-junit").get())
                "androidTestImplementation"(libs.findLibrary("androidx-test-runner").get())
                "debugImplementation"(libs.findLibrary("androidx-compose-ui-test-manifest").get())
            }

            val featurePath = path
            configurations.configureEach {
                dependencies.withType<ProjectDependency>().configureEach {
                    // A module depending on itself is normal (tests, Hilt); another feature is not.
                    if (path.startsWith(":feature:") && path != featurePath) {
                        throw GradleException(
                            "$featurePath must not depend on $path. " +
                                "Features only talk to :core:* modules.",
                        )
                    }
                }
            }
        }
    }
}
