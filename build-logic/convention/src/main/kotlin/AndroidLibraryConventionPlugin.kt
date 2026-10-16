import com.android.build.api.dsl.LibraryExtension
import io.github.zav.pdfbanao.buildlogic.PdfBanaoSdk
import io.github.zav.pdfbanao.buildlogic.configureKotlinAndroid
import io.github.zav.pdfbanao.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/** Recipe for every `:core:*` and `:feature:*` module. */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                testOptions.targetSdk = PdfBanaoSdk.TARGET
                lint.targetSdk = PdfBanaoSdk.TARGET
            }

            dependencies {
                "testImplementation"(libs.findLibrary("junit").get())
            }
        }
    }
}
