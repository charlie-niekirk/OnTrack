package me.cniekirk.ontrack

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project

private const val RELEASE_CONFIG_NAME = "release"

internal fun Project.configureSigning(applicationExtension: ApplicationExtension) {
    applicationExtension.signingConfigs.create(RELEASE_CONFIG_NAME) {
        // It's a good practice to only configure signing if the required
        // environment variables are present. This prevents build failures in
        // local development environments where these secrets might not be set.
        val keyStorePath = System.getenv("SIGNING_KEY_STORE_PATH")
        if (keyStorePath != null) {
            storeFile = file(keyStorePath)
            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
            keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
        } else {
            // Log a warning if the signing information is not available.
            // The build will produce an unsigned release artifact.
            logger.warn(
                """
                Release signing configuration not found.
                Set the following environment variables to sign the release build:
                SIGNING_KEY_STORE_PATH, SIGNING_STORE_PASSWORD, SIGNING_KEY_ALIAS, SIGNING_KEY_PASSWORD
                """.trimIndent()
            )
        }

        // Apply the "release" signing configuration to the "release" build type.
        applicationExtension.buildTypes.getByName(RELEASE_CONFIG_NAME) {
            isMinifyEnabled = true
            proguardFiles(
                applicationExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = applicationExtension.signingConfigs.getByName(RELEASE_CONFIG_NAME)
        }
    }
}