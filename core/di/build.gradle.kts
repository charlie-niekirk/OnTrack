plugins {
    alias(libs.plugins.ontrack.android.library)
}

android {
    namespace = "me.cniekirk.ontrack.core.di"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.work.runtime.ktx)
}