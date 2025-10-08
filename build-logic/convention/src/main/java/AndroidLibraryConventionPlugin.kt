import com.android.build.gradle.LibraryExtension
import me.cniekirk.ontrack.androidTestImplementation
import me.cniekirk.ontrack.configureKotlinAndroid
import me.cniekirk.ontrack.implementation
import me.cniekirk.ontrack.libs
import me.cniekirk.ontrack.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("dev.zacsweers.metro")
                apply("com.google.devtools.ksp")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 36
            }
            dependencies {
                implementation(libs.findLibrary("com.jakewharton.timber").get())
                testImplementation(kotlin("test"))
                androidTestImplementation(kotlin("test"))
            }
        }
    }
}