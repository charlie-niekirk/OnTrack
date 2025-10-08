package me.cniekirk.ontrack.extensions

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create

abstract class ComposeConventionExtension {

    abstract val compilerMetricsEnabled: Property<Boolean>

    companion object {
        fun register(project: Project): ComposeConventionExtension {
            val extension = project.extensions.create<ComposeConventionExtension>("composeConvention")
            extension.compilerMetricsEnabled.convention(false)
            return extension
        }
    }
}