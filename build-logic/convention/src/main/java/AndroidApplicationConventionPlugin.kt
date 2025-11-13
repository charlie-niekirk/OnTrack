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

                // Create benchmark build type for baseline profile testing
                buildTypes {
                    create("benchmark") {
                        // Benchmark builds should be similar to release but with debug signing
                        initWith(getByName("release"))
                        signingConfig = signingConfigs.getByName("debug")
                        matchingFallbacks += listOf("release")
                        isDebuggable = false
                        isMinifyEnabled = true
                        isShrinkResources = true
                    }
                }
            }
        }
    }
}