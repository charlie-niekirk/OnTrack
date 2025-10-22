plugins {
    alias(libs.plugins.ontrack.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
}

android {
    namespace = "me.cniekirk.ontrack.core.navigation"
}

dependencies {
    implementation(project(":core:domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.org.jetbrains.kotlinx.serialization.json)
}