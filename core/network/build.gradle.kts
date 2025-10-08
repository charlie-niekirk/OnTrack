plugins {
    alias(libs.plugins.ontrack.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
}

android {
    namespace = "me.cniekirk.ontrack.core.network"
}

dependencies {
    implementation(project(":core:di"))
    implementation(libs.com.squareup.okhttp3)
    implementation(libs.com.squareup.okhttp3.logging.interceptor)

    implementation(libs.com.squareup.retrofit2)
    implementation(libs.com.squareup.retrofit2.converter.kotlinx.serialization)
    implementation(libs.org.jetbrains.kotlinx.serialization.json)
}