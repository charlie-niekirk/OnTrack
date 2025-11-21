import com.android.build.api.dsl.ApplicationExtension
import me.cniekirk.ontrack.configureKotlinAndroid
import me.cniekirk.ontrack.configureSigning
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("dev.zacsweers.metro")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                configureSigning(this)
                defaultConfig.targetSdk = 36

                // Create acceptance flavor for baseline profile testing
                // Baseline profile gradle tasks are only created for flavors, not build types
                flavorDimensions += "version"
                productFlavors {
                    create("acceptance") {
                        dimension = "version"
                        // The acceptance flavor uses the acceptance source set which contains
                        // TestOnTrackApp with fake implementations for baseline profile testing
                    }
                    create("prod") {
                        dimension = "version"
                        // Production flavor uses the main source set
                        isDefault = true
                    }
                }
            }
        }
    }
}