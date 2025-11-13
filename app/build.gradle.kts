plugins {
    alias(libs.plugins.ontrack.android.application.compose)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
    alias(libs.plugins.android.application)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "me.cniekirk.ontrack"

    defaultConfig {
        applicationId = "me.cniekirk.ontrack"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    metro {
        debug.set(true)
    }
}

// Configure baseline profile to use the benchmark variant
baselineProfile {
    baselineProfileRulesRewrite = true

    filter {
        include("me.cniekirk.ontrack.**")
    }
}

// Enable baseline profile generation for the benchmark variant
androidComponents {
    onVariants { variant ->
        if (variant.name == "benchmark") {
            // This ensures the generateBenchmarkBaselineProfile task is created
            variant.enableAndroidTestCoverage = false
        }
    }
}

dependencies {
    implementation(project(":core:compose"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:di"))
    implementation(project(":core:domain"))
    implementation(project(":core:navigation"))
    implementation(project(":core:network"))
    implementation(project(":core:platform"))

    implementation(project(":feature:home"))
    implementation(project(":feature:servicedetail"))
    implementation(project(":feature:servicelist"))
    implementation(project(":feature:stationsearch"))

    implementation(libs.org.orbit.mvi.core)
    implementation(libs.org.orbit.mvi.compose)
    implementation(libs.org.orbit.mvi.viewmodel)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.org.jetbrains.kotlinx.serialization.json)

    benchmarkImplementation(libs.com.michael.bull.kotlin.result)

    implementation(libs.com.jakewharton.timber)
    implementation(libs.androidx.profileinstaller)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    "baselineProfile"(project(":baselineprofile"))

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}