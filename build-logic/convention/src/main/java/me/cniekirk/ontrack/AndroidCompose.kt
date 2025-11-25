package me.cniekirk.ontrack

import com.android.build.api.dsl.CommonExtension
import me.cniekirk.ontrack.extensions.ComposeConventionExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

/**
 * Configure Compose-specific options
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    composeConventionExtension: ComposeConventionExtension,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        dependencies {
            val bom = libs.findLibrary("androidx-compose-bom").get()
            add("implementation", platform(bom))
            add("implementation", libs.findLibrary("androidx-ui").get())
            add("implementation", libs.findLibrary("androidx-ui-graphics").get())
            add("implementation", libs.findLibrary("androidx-ui-tooling").get())
            add("implementation", libs.findLibrary("androidx-ui-tooling-preview").get())
            add("implementation", libs.findLibrary("androidx-material3").get())
            add("implementation", libs.findLibrary("androidx-compose-material-icons-extended").get())

            add("implementation", libs.findLibrary("androidx-activity-compose").get())
            add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())
//            add("implementation", libs.findLibrary("androidx-compose-material-icons-extended").get())

            add("androidTestImplementation", platform(bom))
            add("androidTestImplementation", libs.findLibrary("androidx-ui-test-junit4").get())
            add("debugImplementation", libs.findLibrary("androidx-ui-test-manifest").get())
        }
    }

    with(pluginManager) {
        apply("org.jetbrains.kotlin.plugin.compose")
    }

    val composeCompilerExtension = extensions.getByType<ComposeCompilerGradlePluginExtension>()

    val metricsEnabledProvider = composeConventionExtension.compilerMetricsEnabled
    val reportsDir = project.layout.buildDirectory.dir("compose-reports")
    val metricsDir = project.layout.buildDirectory.dir("compose-metrics")

    composeCompilerExtension.metricsDestination.set(metricsDir.flatMap {
        if (metricsEnabledProvider.get()) providers.provider { it } else providers.provider { null }
    })
    composeCompilerExtension.reportsDestination.set(reportsDir.flatMap {
        if (metricsEnabledProvider.get()) providers.provider { it } else providers.provider { null }
    })
}