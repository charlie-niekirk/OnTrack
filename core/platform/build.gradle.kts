plugins {
    alias(libs.plugins.ontrack.android.library)
}

android {
    namespace = "me.cniekirk.ontrack.core.platform"
}

dependencies {
    implementation(project(":core:di"))
}