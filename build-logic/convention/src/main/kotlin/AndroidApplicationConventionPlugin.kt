import com.android.build.api.dsl.ApplicationExtension
import io.github.zav.pdfbanao.buildlogic.PdfBanaoSdk
import io.github.zav.pdfbanao.buildlogic.configureAndroidCompose
import io.github.zav.pdfbanao.buildlogic.configureAndroidLint
import io.github.zav.pdfbanao.buildlogic.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/** Recipe for the `:app` module. */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                configureAndroidCompose(this)
                configureAndroidLint(this)
                defaultConfig.targetSdk = PdfBanaoSdk.TARGET
            }
        }
    }
}
