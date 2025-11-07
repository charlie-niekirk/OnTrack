plugins {
    alias(libs.plugins.ontrack.jvm.library)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
}

dependencies {
    implementation(libs.org.jetbrains.kotlinx.serialization.json)
    implementation(libs.com.michael.bull.kotlin.result)
}