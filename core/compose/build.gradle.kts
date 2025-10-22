plugins {
    alias(libs.plugins.ontrack.android.library.compose)
}

android {
    namespace = "me.cniekirk.ontrack.core.compose"
}

dependencies {
    implementation(project(":core:di"))
    implementation(project(":core:navigation"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui.text.google.fonts)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
}