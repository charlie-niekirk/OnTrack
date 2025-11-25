import com.android.build.api.dsl.LibraryExtension
import me.cniekirk.ontrack.androidTestImplementation
import me.cniekirk.ontrack.implementation
import me.cniekirk.ontrack.libs
import me.cniekirk.ontrack.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("ontrack.android.library.compose")
            }

            extensions.configure<LibraryExtension> {
                @Suppress("UnstableApiUsage")
                testOptions {
                    unitTests {
                        isIncludeAndroidResources = true
                    }
                }

                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
            }

            dependencies {
                implementation(project(":core:domain"))
                implementation(project(":core:compose"))
                implementation(project(":core:di"))
                implementation(project(":core:navigation"))
                implementation(project(":core:platform"))

                implementation(libs.findLibrary("androidx-navigation3-runtime").get())
                implementation(libs.findLibrary("androidx-navigation3-ui").get())
                implementation(libs.findLibrary("androidx-lifecycle-viewmodel-navigation3").get())

                implementation(libs.findLibrary("org.orbit.mvi.core").get())
                implementation(libs.findLibrary("org.orbit.mvi.compose").get())
                implementation(libs.findLibrary("org.orbit.mvi.viewmodel").get())

                implementation(libs.findLibrary("com.michael.bull.kotlin.result").get())

                testImplementation(kotlin("test"))

                testImplementation(libs.findLibrary("org.orbit.mvi.test").get())
                testImplementation(libs.findLibrary("io.mockk").get())
                testImplementation(libs.findLibrary("androidx.junit").get())

                androidTestImplementation(kotlin("test"))
            }
        }
    }
}